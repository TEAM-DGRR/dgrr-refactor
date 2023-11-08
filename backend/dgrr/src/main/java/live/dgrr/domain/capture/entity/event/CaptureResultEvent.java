package live.dgrr.domain.capture.entity.event;

import live.dgrr.domain.capture.entity.CaptureResult;

public record CaptureResultEvent(String memberOneId, String memberTwoId, CaptureResult captureResult) {

}
