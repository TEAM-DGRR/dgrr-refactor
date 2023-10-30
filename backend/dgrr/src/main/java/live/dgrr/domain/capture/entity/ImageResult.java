package live.dgrr.domain.capture.entity;

import lombok.*;
import org.springframework.messaging.handler.annotation.Header;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageResult {

    private boolean success;
    private String emotion;
    private double probability;
    private double smileProbability;
    private String encodedImage;
    private Header header;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Header {
        private int round;
        private String gameSessionId;
    }

}