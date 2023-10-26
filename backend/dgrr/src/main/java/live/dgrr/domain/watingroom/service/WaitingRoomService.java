package live.dgrr.domain.watingroom.service;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.watingroom.dto.response.WaitingMemberInfoResponseDto;
import live.dgrr.domain.watingroom.entity.WaitingMember;
import live.dgrr.domain.watingroom.entity.WaitingRoom;
import live.dgrr.domain.watingroom.repository.WaitingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final int ROOM_MAX_NUM = 2;

    private final WaitingRoomRepository waitingRoomRepository;

    public WaitingRoom findWaitingRoomById(int roomId) {
        return waitingRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());
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

    public WaitingMemberInfoResponseDto enterWaitingRoom(int roomId, Member member) {
        //방이 존재 하는지 확인
        WaitingRoom waitingRoom = findWaitingRoomById(roomId);

        //해당 방에 중복된 사람이 있는지 확인
        checkWaitingRoomDuplicate(waitingRoom, member.getMemberId());

        //사용자 생성
        WaitingMember waitingMember = WaitingMember.of(member.getMemberId(), member.getNickname(), member.getProfileImage(), false);

        //방 참여
        saveToWaitingRoom(waitingRoom, waitingMember);

        return WaitingMemberInfoResponseDto.of(roomId, waitingMember);

    }

    private void saveToWaitingRoom(WaitingRoom waitingRoom, WaitingMember waitingMember) {
        if (waitingRoom.getWaitingMemberList() !=null && waitingRoom.getWaitingMemberList().size() >= ROOM_MAX_NUM) {
            throw new RuntimeException("참여자 정원이 다 찼습니다.");
        }

        waitingRoom.addMember(waitingMember);
        waitingRoomRepository.save(waitingRoom);
    }

    private void checkWaitingRoomDuplicate(WaitingRoom waitingRoom, Long memberId) {
        if (waitingRoom.getWaitingMemberList() != null && waitingRoom.getWaitingMemberList().stream()
                .anyMatch(member -> member.waitingMemberId().equals(memberId))) {
            throw new RuntimeException("이미 대기실에 존재합니다.");
        }
    }


}
