package live.dgrr.domain.capture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.dgrr.domain.capture.entity.ImageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageProcessingService {

    public void processingCaptureImage(String data) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        ImageResult imageResult = objectMapper.readValue(data, ImageResult.class);
        System.out.println(imageResult.toString());

    }

}