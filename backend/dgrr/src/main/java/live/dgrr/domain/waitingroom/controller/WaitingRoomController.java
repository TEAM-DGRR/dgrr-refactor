package live.dgrr.domain.waitingroom.controller;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.service.MemberService;
import live.dgrr.domain.waitingroom.dto.response.WaitingMemberInfoResponseDto;
import live.dgrr.domain.waitingroom.entity.MemberRoomMapping;
import live.dgrr.domain.waitingroom.service.MemberRoomMappingService;
import live.dgrr.domain.waitingroom.service.WaitingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-room")
public class WaitingRoomController {

    private final WaitingRoomService waitingRoomService;
    private final MemberService memberService;
    private final MemberRoomMappingService memberRoomMappingService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ResponseEntity<Integer> createWaitingRoom () {
        log.info("WaitingRoomController - createWaitingRoom");
        int roomId = waitingRoomService.createWaitingRoom();
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    @MessageMapping("/room-enter")
    public void enterWaitingRoom (Principal principal, @Payload int roomId) {
        log.info("WaitingRoomController - enterWaitingRoom by : {}, roomId : {}", principal.getName(), roomId);

        Member member = memberService.findMemberById(Long.parseLong(principal.getName()));
        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.enterWaitingRoom(roomId, member);

        waitingRoomService.findWaitingRoomById(roomId)
                .getWaitingMemberList()
                .stream()
                .map(waitingMember -> String.valueOf(waitingMember.getWaitingMemberId()))
                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, "/recv/room-enter", waitingMemberInfoDto));
    }

    @MessageMapping("/room-exit")
    public void exitWaitingRoom (Principal principal) {
        log.info("WaitingRoomController - exitWaitingRoom by : {}", principal.getName());

        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(Long.parseLong(principal.getName()));
        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.exitWaitingRoom(memberRoomMapping.getRoomId(), Long.valueOf(principal.getName()));

        waitingRoomService.findWaitingRoomById(waitingMemberInfoDto.roomId())
                .getWaitingMemberList()
                .stream()
                .map(waitingMember -> String.valueOf(waitingMember.getWaitingMemberId()))
                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, "/recv/room-exit", waitingMemberInfoDto));
    }

    @MessageMapping("/room-ready")
    public void readyWaitingRoom (Principal principal) {
        log.info("WaitingRoomController - readyWaitingRoom");
        //TODO: mebmerId 추후 수정, 방 참여자에게 전부 메세지 보내기
        Long memberId = 1L;
        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(memberId);

        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.readyWaitingRoom(memberRoomMapping.getRoomId(),memberId);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/recv/room-ready", waitingMemberInfoDto);
    }

    @MessageMapping("/room-start")
    public void startWaitingRoom (Principal principal) {
        log.info("WaitingRoomController - startWaitingRoom by {} : ", principal.getName());
        //TODO: mebmerId 추후 수정, 방 참여자에게 전부 메세지 보내기
        Long memberId = 1L;
        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(memberId);

        waitingRoomService.startWaitingRoom(memberRoomMapping.getRoomId(),memberId);

    }
}
