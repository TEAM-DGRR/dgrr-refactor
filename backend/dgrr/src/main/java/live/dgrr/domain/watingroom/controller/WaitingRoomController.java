package live.dgrr.domain.watingroom.controller;

import live.dgrr.domain.watingroom.service.WaitingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-room")
public class WaitingRoomController {

    private final WaitingRoomService waitingRoomService;

    @PostMapping("")
    public ResponseEntity<Integer> createWaitingRoom () {
        log.info("WaitingRoomController - createWaitingRoom");
        int roomId = waitingRoomService.createWaitingRoom();
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }
}
