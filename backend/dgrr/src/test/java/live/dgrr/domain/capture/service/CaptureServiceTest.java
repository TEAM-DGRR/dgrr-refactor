package live.dgrr.domain.capture.service;

import live.dgrr.domain.capture.entity.CaptureResult;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.SecondRoundOverEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CaptureServiceTest {

    @InjectMocks
    CaptureService captureService;

    @Mock
    ApplicationEventPublisher publisher;

    @DisplayName("enocdedImage 받을 때 - 1Round")
    @Test
    void deSerializationCaptureTest_1Round() {
        String captureJson =
                "{" +
                        "\"success\":true," +
                        "\"emotion\":\"Smile\"," +
                        "\"probability\":0.95," +
                        "\"smileProbability\":0.9," +
                        "\"encodedImage\":\"AABS@9XX1234\"," +
                        "\"header\":" +
                        "{" +
                        "\"round\":1," +
                        "\"gameSessionId\":\"12345\"" +
                        "}" +
                        "}";


        CaptureResult captureResult = captureService.deSerializationCapture(captureJson);

        assertNotNull(captureResult);
        assertTrue(captureResult.isSuccess());
        assertEquals("Smile", captureResult.getEmotion());
        assertEquals(0.95, captureResult.getProbability());
        assertEquals(0.9, captureResult.getSmileProbability());
        assertEquals("AABS@9XX1234", captureResult.getEncodedImage());
        assertNotNull(captureResult.getHeader());
        assertEquals(1, captureResult.getHeader().getRound());
        assertEquals("12345", captureResult.getHeader().getGameSessionId());
        verify(publisher).publishEvent(any(FirstRoundOverEvent.class));
        verify(publisher, times(0)).publishEvent(any(SecondRoundOverEvent.class));


    }

    @DisplayName("enocdedImage 받을 때 - 2Round")
    @Test
    void deSerializationCaptureTest_2Round() {
        String captureJson =
                "{" +
                        "\"success\":true," +
                        "\"emotion\":\"Smile\"," +
                        "\"probability\":0.68," +
                        "\"smileProbability\":0.68," +
                        "\"encodedImage\":\"AABS@9XX1234\"," +
                        "\"header\":" +
                        "{" +
                        "\"round\":2," +
                        "\"gameSessionId\":\"abcdefg\"" +
                        "}" +
                        "}";


        CaptureResult captureResult = captureService.deSerializationCapture(captureJson);

        assertNotNull(captureResult);
        assertTrue(captureResult.isSuccess());
        assertEquals("Smile", captureResult.getEmotion());
        assertEquals(0.68, captureResult.getProbability());
        assertEquals(0.68, captureResult.getSmileProbability());
        assertEquals("AABS@9XX1234", captureResult.getEncodedImage());
        assertNotNull(captureResult.getHeader());
        assertEquals(2, captureResult.getHeader().getRound());
        assertEquals("abcdefg", captureResult.getHeader().getGameSessionId());
        verify(publisher, times(0)).publishEvent(any(FirstRoundOverEvent.class));
        verify(publisher).publishEvent(any(SecondRoundOverEvent.class));

    }

    @DisplayName("encodedImage 안받을 때")
    @Test
    void deSerializationCaptureTest_SmileBelowThreshold() {

        String captureJson =
                "{" +
                        "\"success\":true," +
                        "\"emotion\":\"Smile\"," +
                        "\"probability\":0.4," +
                        "\"smileProbability\":0.4," +
                        "\"header\":" +
                        "{" +
                        "\"round\":1," +
                        "\"gameSessionId\":\"12345\"" +
                        "}" +
                        "}";
        CaptureResult captureResult = captureService.deSerializationCapture(captureJson);

        assertNotNull(captureResult);
        assertTrue(captureResult.isSuccess());
        assertEquals("Smile", captureResult.getEmotion());
        assertEquals(0.4, captureResult.getProbability());
        assertEquals(0.4, captureResult.getSmileProbability());
        assertNull(captureResult.getEncodedImage());
        assertNotNull(captureResult.getHeader());
        assertEquals(1, captureResult.getHeader().getRound());
        assertEquals("12345", captureResult.getHeader().getGameSessionId());
        verify(publisher, times(0)).publishEvent(any(FirstRoundOverEvent.class));
        verify(publisher, times(0)).publishEvent(any(SecondRoundOverEvent.class));
    }

}