package live.dgrr.domain.capture.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CaptureResultResponse {

    private boolean success;
    private String emotion;
    private double probability;
    private double smileProbability;

}
