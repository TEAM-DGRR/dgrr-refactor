package live.dgrr.domain.capture.controller;

import live.dgrr.domain.capture.dto.event.CaptureResultResponseEvent;
import live.dgrr.domain.capture.service.CaptureService;

import live.dgrr.domain.capture.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequiredArgsConstructor

public class CaptureController {
    private final CaptureService captureService;
    private final SimpMessagingTemplate template;
    private final S3Service s3Service;
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


    @PostMapping("/api/v1/file/upload")
    public ResponseEntity<URL> uploadFile(MultipartFile file) {
        return ResponseEntity.ok(s3Service.uploadImage(file));
    }



}
