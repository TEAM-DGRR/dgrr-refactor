package live.dgrr.domain.capture.controller;

import live.dgrr.domain.capture.entity.CaptureResult;
import live.dgrr.domain.capture.entity.event.CaptureResultEvent;
import live.dgrr.domain.capture.service.CaptureService;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class CaptureController {
    private final CaptureService captureService;
    private final SimpMessagingTemplate template;
    private static final String CAPTURE_RESULT_DEST = "recv/cpature-result";


    @MessageMapping("/capture-image")
    public void receiveCapture(@Payload String capture) {
        captureService.deSerializationCapture(capture);

    }

    @EventListener
    public void sendCaptureResultToUsers(CaptureResultEvent event) {
        template.convertAndSendToUser(event.memberOneId(), CAPTURE_RESULT_DEST, event.captureResult());
        template.convertAndSendToUser(event.memberTwoId(), CAPTURE_RESULT_DEST, event.captureResult());
    }

}
