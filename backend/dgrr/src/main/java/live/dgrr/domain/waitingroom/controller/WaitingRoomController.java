package live.dgrr.domain.waitingroom.controller;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.service.MemberService;
import live.dgrr.domain.waitingroom.dto.response.WaitingMemberInfoResponseDto;
import live.dgrr.domain.waitingroom.entity.MemberRoomMapping;
import live.dgrr.domain.waitingroom.entity.WaitingMember;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-room")
public class WaitingRoomController {

    private final WaitingRoomService waitingRoomService;
    private final MemberService memberService;
    private final MemberRoomMappingService memberRoomMappingService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String WAITING_ROOM_ENTER = "/recv/room-enter";
    private static final String WAITING_ROOM_EXIT = "/recv/room-exit";
    private static final String WAITING_ROOM_READY = "/recv/room-ready";

    @PostMapping
    public ResponseEntity<Integer> createWaitingRoom () {
        return new ResponseEntity<>(waitingRoomService.createWaitingRoom(), HttpStatus.OK);
    }

    @MessageMapping("/room-enter")
    public void enterWaitingRoom (Principal principal, @Payload int roomId) {

        Member member = memberService.findMemberById(Long.parseLong(principal.getName()));
        List<WaitingMemberInfoResponseDto> waitingMemberInfoDtoList = waitingRoomService.enterWaitingRoom(roomId, member);

        waitingRoomService.findWaitingRoomById(roomId)
                .getWaitingMemberList()
                .stream()
                .map(WaitingMember::getWaitingMemberId)
                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, WAITING_ROOM_ENTER, waitingMemberInfoDtoList));
    }

    @MessageMapping("/room-exit")
    public void exitWaitingRoom (Principal principal) {

        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(Long.parseLong(principal.getName()));
        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.exitWaitingRoom(memberRoomMapping.getRoomId(), principal.getName());

        waitingRoomService.findWaitingRoomById(waitingMemberInfoDto.roomId())
                .getWaitingMemberList()
                .stream()
                .map(WaitingMember::getWaitingMemberId)
                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, WAITING_ROOM_EXIT, waitingMemberInfoDto));
    }

    @MessageMapping("/room-ready")
    public void readyWaitingRoom (Principal principal) {

        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(Long.parseLong(principal.getName()));
        WaitingMemberInfoResponseDto waitingMemberInfoDto = waitingRoomService.readyWaitingRoom(memberRoomMapping.getRoomId(),principal.getName());

        waitingRoomService.findWaitingRoomById(waitingMemberInfoDto.roomId())
                .getWaitingMemberList()
                .stream()
                .map(WaitingMember::getWaitingMemberId)
                .forEach(userId -> simpMessagingTemplate.convertAndSendToUser(userId, WAITING_ROOM_READY, waitingMemberInfoDto));
    }

    @MessageMapping("/room-start")
    public void startWaitingRoom (Principal principal) {

        MemberRoomMapping memberRoomMapping = memberRoomMappingService.findRoomIdByMemberId(Long.parseLong(principal.getName()));
        waitingRoomService.startWaitingRoom(memberRoomMapping.getRoomId(),principal.getName());

    }
}
