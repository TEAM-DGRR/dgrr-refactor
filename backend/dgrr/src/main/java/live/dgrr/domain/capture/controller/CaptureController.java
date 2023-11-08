package live.dgrr.domain.capture.controller;

import live.dgrr.domain.capture.dto.event.CaptureResultResponseEvent;
import live.dgrr.domain.capture.service.CaptureService;

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
    private static final String CAPTURE_RESULT_DEST = "/recv/capture-result";


    @MessageMapping("/capture-image")
    public void receiveCapture(@Payload String capture) {
        captureService.deSerializationCapture(capture);

    }

    @EventListener
    public void sendCaptureResultToUsers(CaptureResultResponseEvent event) {
        template.convertAndSendToUser(event.memberOneId(), CAPTURE_RESULT_DEST, event.captureResultResponse());
        template.convertAndSendToUser(event.memberTwoId(), CAPTURE_RESULT_DEST, event.captureResultResponse());
    }

}
