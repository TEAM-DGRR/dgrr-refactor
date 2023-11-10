package live.dgrr.domain.capture.service;

import live.dgrr.domain.capture.entity.Highlight;
import live.dgrr.domain.capture.entity.HighlightID;
import live.dgrr.domain.capture.repository.HighlightRepository;
import live.dgrr.global.exception.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HighlightServiceTest {

    @InjectMocks
    private HighlightServiceImpl highlightServiceImpl;

    @Spy
    private HighlightRepository highlightRepository;

    @Mock
    private S3Service s3Service;

    private URL mockUrl;
    private String gameRoomId;
    private int round;
    private HighlightID highlightID;
    private String encodedImage;

    @BeforeEach
    void setUp() throws MalformedURLException {
        mockUrl = new URL("https://example.com/highlight.jpg");
        gameRoomId = "gameRoom1";
        round = 1;
        highlightID = new HighlightID(gameRoomId, round);
        encodedImage = "imageBase64=";
    }

    @Test
    @DisplayName("Highlight 객체 저장 - 성공")
    void testSaveHighlightSuccess() {
        when(highlightRepository.findById(highlightID)).thenReturn(Optional.empty());
        when(s3Service.uploadBase64EncodedImage(encodedImage)).thenReturn(mockUrl);

        highlightServiceImpl.saveHighlight(highlightID, encodedImage);

        ArgumentCaptor<Highlight> highlightCaptor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(highlightCaptor.capture());

        Highlight capturedHighlight = highlightCaptor.getValue();
        assertEquals(mockUrl, capturedHighlight.getCaptureUrl());
        assertEquals(highlightID, capturedHighlight.getHighlightID());
    }

    @Test
    @DisplayName("Highlight URL 가져오기 - 성공")
    void testGetHighlightUrlSuccess() {
        Highlight mockHighlight = new Highlight(highlightID, mockUrl);
        when(highlightRepository.findById(highlightID)).thenReturn(Optional.of(mockHighlight));

        String resultUrl = highlightServiceImpl.highlightImage(gameRoomId, round);

        assertEquals(mockUrl.toString(), resultUrl);
    }

    @Test
    @DisplayName("Highlight URL 가져오기 - 실패 (Highlight 찾을 수 없음)")
    void testGetHighlightUrlFailure() {
        when(highlightRepository.findById(highlightID)).thenReturn(Optional.empty());

        assertThrows(GameException.class, () -> highlightServiceImpl.highlightImage(gameRoomId, round));
    }
}
