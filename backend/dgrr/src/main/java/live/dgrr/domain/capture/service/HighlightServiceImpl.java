package live.dgrr.domain.capture.service;

import live.dgrr.domain.capture.entity.Highlight;
import live.dgrr.domain.capture.entity.HighlightID;
import live.dgrr.domain.capture.repository.HighlightRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HighlightServiceImpl implements IHighlightService {

    private final HighlightRepository highlightRepository;
    private final S3Service s3Service;

    @Override
    public void saveHighlight(HighlightID highlightID, String encodedImage) {
        Optional<Highlight> optionalHighlight = highlightRepository.findById(highlightID);
        if (optionalHighlight.isPresent()) return;

        URL captureUrl = s3Service.uploadBase64EncodedImage(encodedImage);
        Highlight highlight = new Highlight(highlightID, captureUrl);


        highlightRepository.save(highlight);
    }

    @Override
    public String highlightImage(String gameRoomId, int round) {
        HighlightID highlightID = new HighlightID(gameRoomId, round);
        Highlight highlight = highlightRepository.findById(highlightID).orElseThrow(() -> new GameException(ErrorCode.HIGHLIGHT_NOT_FOUND));

        return highlight.getCaptureUrl().toString();

    }


}
