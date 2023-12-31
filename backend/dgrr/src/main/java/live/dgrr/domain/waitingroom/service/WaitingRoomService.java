package live.dgrr.domain.waitingroom.service;

import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.waitingroom.dto.response.WaitingMemberInfoResponseDto;
import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import live.dgrr.domain.waitingroom.entity.MemberRoomMapping;
import live.dgrr.domain.waitingroom.entity.WaitingMember;
import live.dgrr.domain.waitingroom.entity.WaitingRoom;
import live.dgrr.domain.waitingroom.repository.MemberRoomMappingRepository;
import live.dgrr.domain.waitingroom.repository.WaitingRoomRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final int ROOM_MAX_NUM = 2;

    private final WaitingRoomRepository waitingRoomRepository;
    private final MemberRoomMappingRepository memberRoomMappingRepository;
    private final ApplicationEventPublisher publisher;

    public WaitingRoom findWaitingRoomById(int roomId) {
        return waitingRoomRepository.findById(roomId).orElseThrow(() -> new GeneralException(ErrorCode.WAITING_ROOM_NOT_FOUND));
    }

    public int createWaitingRoom() {
        //TODO: waitingRoomId 확인 로직 수정 필요
        int waitingRoomId;
        do {
            waitingRoomId = generateRoomId();
        } while (isWaitingRoomIdExists(waitingRoomId));

        WaitingRoom waitingRoom = waitingRoomRepository.save(new WaitingRoom(waitingRoomId));
        return  waitingRoom.getRoomId();
    }

    public static int generateRoomId() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    public boolean isWaitingRoomIdExists(int waitingRoomId) {
        return waitingRoomRepository.existsById(waitingRoomId);
    }

    public List<WaitingMemberInfoResponseDto> enterWaitingRoom(int roomId, Member member) {
        //방이 존재 하는지 확인
        WaitingRoom waitingRoom = findWaitingRoomById(roomId);

        //해당 방에 중복된 사람이 있는지 확인
        checkWaitingRoomDuplicate(waitingRoom, String.valueOf(member.getMemberId()));

        //사용자 생성
        WaitingMember newWaitingMember = WaitingMember.of(String.valueOf(member.getMemberId()), member.getNickname(), member.getProfileImage(), false);

        //방 참여
        saveToWaitingRoom(waitingRoom, newWaitingMember);

        //방 안에 있는 모든 참가자들 리스트로 반환
        List<WaitingMemberInfoResponseDto> waitingMembersInfoList = waitingRoom.getWaitingMemberList().stream()
                .map(waitingMember -> WaitingMemberInfoResponseDto.of(roomId, waitingMember))
                .collect(Collectors.toList());

        return waitingMembersInfoList;

    }

    private void saveToWaitingRoom(WaitingRoom waitingRoom, WaitingMember waitingMember) {
        if(checkMemberFull(waitingRoom)) {
            throw new GeneralException(ErrorCode.MAX_MEMBER_ALREADY_EXIST);
        }

        waitingRoom.addMember(waitingMember);
        waitingRoomRepository.save(waitingRoom);
        memberRoomMappingRepository.save(new MemberRoomMapping(waitingMember.getWaitingMemberId(),waitingRoom.getRoomId()));
    }

    private boolean checkMemberFull(WaitingRoom waitingRoom) {
        return waitingRoom.getWaitingMemberList() != null && waitingRoom.getWaitingMemberList().size() >= ROOM_MAX_NUM;
    }

    private void checkWaitingRoomDuplicate(WaitingRoom waitingRoom, String memberId) {
        if (waitingRoom.getWaitingMemberList() != null && waitingRoom.getWaitingMemberList().stream()
                .anyMatch(member -> member.getWaitingMemberId().equals(memberId))) {
            throw new GeneralException(ErrorCode.MEMBER_ALREADY_EXIST);
        }
    }


    public WaitingMemberInfoResponseDto readyWaitingRoom(int roomId, String memberId) {
        WaitingRoom waitingRoom = findWaitingRoomById(roomId);
        List<WaitingMember> waitingMembers = waitingRoom.getWaitingMemberList();
        WaitingMember waitingMember = new WaitingMember();

        for(int j = 0; j < waitingMembers.size(); j++) {
            if(waitingMembers.get(j).getWaitingMemberId().equals(memberId)) {
                waitingMember = waitingRoom.getWaitingMemberList().get(j);
                waitingMember.toggleReady();
                waitingRoomRepository.save(waitingRoom);
            }
        }

        return WaitingMemberInfoResponseDto.of(roomId, waitingMember);

    }

    public void startWaitingRoom(int roomId, String memberId) {
        WaitingRoom waitingRoom = findWaitingRoomById(roomId);
        List<WaitingMember> waitingMembers = waitingRoom.getWaitingMemberList();

        checkGameStart(waitingRoom, waitingMembers, memberId);

        //게임 시작 로직
        waitingRoom.gameStart();
        waitingRoomRepository.save(waitingRoom);
        Collections.shuffle(waitingMembers);
        Collections.shuffle(waitingMembers);
        publisher.publishEvent(new GameStartEvent(waitingMembers.get(0).getWaitingMemberId(), waitingMembers.get(1).getWaitingMemberId(), GameType.PRIVATE));

    }

    private void checkGameStart(WaitingRoom waitingRoom, List<WaitingMember> waitingMembers, String memberId) {
        //방장인지 확인
        if(!waitingMembers.get(0).getWaitingMemberId().equals(memberId)) {
            throw new GeneralException(ErrorCode.IS_NOT_ROOM_MANAGER);
        }

        //방 안에 2명인지 확인
        if(!checkMemberFull(waitingRoom)) {
            throw new GeneralException(ErrorCode.NOT_ENOUGH_MEMBERS_TO_START);
        }

        //모든 플레이어가 다 준비했는지 확인 (1번 인덱스만 확인)
        if(!waitingMembers.get(1).isReady()) {
            throw new GeneralException(ErrorCode.MEMBER_NOT_READY_YET);
        }

        //이미 시작된 방인지 확인
        if(waitingRoom.isStart()) {
            throw new GeneralException(ErrorCode.WAITING_ROOM_ALREADY_START);
        }

    }

    public WaitingMemberInfoResponseDto exitWaitingRoom(int roomId, String memberId) {

        WaitingRoom waitingRoom = findWaitingRoomById(roomId);
        List<WaitingMember> waitingMembers = waitingRoom.getWaitingMemberList();
        WaitingMember waitingMember = new WaitingMember();

        for(int i = 0; i < waitingMembers.size(); i++) {
            if(waitingMembers.get(i).getWaitingMemberId().equals(memberId)) {

                waitingMember = waitingRoom.getWaitingMemberList().get(i);
                waitingRoom.exitMember(i);
                waitingRoomRepository.save(waitingRoom);
            }
        }

        return WaitingMemberInfoResponseDto.of(roomId, waitingMember);

    }
}
