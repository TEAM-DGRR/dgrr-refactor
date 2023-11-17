package live.dgrr.domain.capture.dto.event;

import live.dgrr.domain.capture.dto.response.CaptureResultResponse;

public record CaptureResultResponseEvent(String memberOneId, String memberTwoId, CaptureResultResponse captureResultResponse) {

}
