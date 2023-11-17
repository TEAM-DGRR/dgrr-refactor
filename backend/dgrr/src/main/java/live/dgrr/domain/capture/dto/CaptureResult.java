package live.dgrr.domain.capture.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CaptureResult {

    private boolean success;
    private String emotion;
    private double probability;
    private double smileProbability;
    private String encodedImage;
    private Header header;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        private int round;
        private String gameSessionId;
    }

}