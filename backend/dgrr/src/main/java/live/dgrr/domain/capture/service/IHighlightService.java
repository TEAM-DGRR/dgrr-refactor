package live.dgrr.domain.capture.service;

import live.dgrr.domain.capture.entity.HighlightID;

public interface HighlightService {
    void saveHighlight(HighlightID highlightID, String encodedImage);

    String highlightImage(String gameRoomId, int round);

}

