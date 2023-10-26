package live.dgrr.domain.watingroom.controller;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.service.MemberService;
import live.dgrr.domain.watingroom.dto.response.WaitingMemberInfoResponseDto;
import live.dgrr.domain.watingroom.entity.WaitingRoom;
import live.dgrr.domain.watingroom.service.WaitingRoomService;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ResponseEntity<Integer> createWaitingRoom () {
        log.info("WaitingRoomController - createWaitingRoom");
        int roomId = waitingRoomService.createWaitingRoom();
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    @MessageMapping("/room-enter")
    public void enterWaitingRoom (Principal principal, @Payload int roomId) {
        log.info("WaitingRoomController - enterWaitingRoom roomId : {}", roomId);

        //TODO: mebmerId 추후 수정
        Long memberId = 3L;
        Member member = memberService.findMemberById(memberId);
        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.enterWaitingRoom(roomId, member);

//      TODO: 경로 수정 시 주석 해제 (방 참여자에게 전부 메세지 보냄)

//        waitingRoomService.findWaitingRoomById(roomId)
//                .getWaitingMemberList()
//                .stream()
//                .map(waitingMember -> String.valueOf(waitingMember.waitingMemberId()))
//                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, "/recv/room-enter", waitingMemberInfoDto));

        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/recv/room-enter", waitingMemberInfoDto);
    }
}
