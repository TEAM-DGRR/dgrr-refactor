package live.dgrr.domain.capture.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.dgrr.domain.capture.service.ImageProcessingService;
import org.json.simple.parser.ParseException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
    private final ImageProcessingService imageProcessingService;

    public ImageController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @MessageMapping("/capture-image")
    public void receiveImageDataFromPythonClient(@Payload String img) throws JsonProcessingException {
        imageProcessingService.processingCaptureImage(img);
    }
}
