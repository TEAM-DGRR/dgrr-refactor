package live.dgrr.domain.capture.service;

import live.dgrr.domain.capture.entity.CaptureResult;
import live.dgrr.domain.capture.entity.event.CaptureResultEvent;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.game.entity.event.SecondRoundOverEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CaptureServiceTest {

    @InjectMocks
    CaptureService captureService;

    @Spy
    GameRoomRepository gameRoomRepository;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    HighlightServiceImpl highlightServiceImpl;

    @Captor
    private ArgumentCaptor<FirstRoundOverEvent> firstRoundOverEventCaptor;

    @Captor
    private ArgumentCaptor<SecondRoundOverEvent> secondRoundOverEventCaptor;

    @Captor
    private ArgumentCaptor<CaptureResultEvent> captureResultEventCaptor;

    private String gameRoomId;
    private GameRoom gameRoom;
    private String memberOneId;
    private String memberTwoId;

    @BeforeEach
    void setUp() {
        gameRoomId = "id";
        memberOneId = "M1";
        memberTwoId = "M2";
        GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Tier.SILVER);
        GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Tier.BRONZE);
        gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND, GameType.RANDOM);

        when(gameRoomRepository.findById(gameRoomId)).thenReturn(Optional.of(gameRoom));
    }

    private CaptureResult processCaptureResult(String captureJson, int expectedRound, boolean expectImage) {
        CaptureResult captureResult = captureService.deSerializationCapture(captureJson);

        assertNotNull(captureResult);
        assertTrue(captureResult.isSuccess());
        assertNotNull(captureResult.getHeader());
        assertEquals(expectedRound, captureResult.getHeader().getRound());
        assertEquals(gameRoomId, captureResult.getHeader().getGameSessionId());

        if (expectImage) {
            assertEquals("AABS@9XX1234", captureResult.getEncodedImage());
        } else {
            assertNull(captureResult.getEncodedImage());
        }

        return captureResult;
    }

    private void verifyCaptureResultEventPublished(CaptureResult captureResult) {
        verify(publisher).publishEvent(captureResultEventCaptor.capture());
        CaptureResultEvent publishedEvent = captureResultEventCaptor.getValue();
        assertThat(publishedEvent.memberOneId()).isEqualTo(memberOneId);
        assertThat(publishedEvent.memberTwoId()).isEqualTo(memberTwoId);
        assertThat(publishedEvent.captureResult()).isEqualTo(captureResult);
    }

    @DisplayName("1라운드 - 웃었을 때")
    @Test
    void deSerializationCaptureTest_1Round_LAUGH() {
        String captureJson = createCaptureJson(1, gameRoomId, true, "Smile", 0.95, 0.95);

        CaptureResult captureResult = processCaptureResult(captureJson, 1, true);
        assertEquals("Smile", captureResult.getEmotion());
        assertEquals(0.95, captureResult.getProbability());
        assertEquals(0.95, captureResult.getSmileProbability());

        verifyCaptureResultEventPublished(captureResult);

        verify(publisher).publishEvent(firstRoundOverEventCaptor.capture());
        verify(publisher, never()).publishEvent(secondRoundOverEventCaptor.capture());
        FirstRoundOverEvent firstRoundOverEvent = firstRoundOverEventCaptor.getValue();
        assertThat(firstRoundOverEvent.gameRoomId()).isEqualTo(gameRoomId);
        assertThat(firstRoundOverEvent.roundResult()).isEqualTo(RoundResult.LAUGH);
    }

    @DisplayName("1라운드 - 안 웃었을 때")
    @Test
    void deSerializationCaptureTest_1Round_NO_LAUGH() {
        String captureJson = createCaptureJson(1, gameRoomId, true, "Neutral", 0.10, 0.10);

        CaptureResult captureResult = processCaptureResult(captureJson, 1, false);
        assertEquals("Neutral", captureResult.getEmotion());
        assertEquals(0.10, captureResult.getProbability());
        assertEquals(0.10, captureResult.getSmileProbability());

        verifyCaptureResultEventPublished(captureResult);

        verify(publisher, never()).publishEvent(firstRoundOverEventCaptor.capture());
        verify(publisher, never()).publishEvent(secondRoundOverEventCaptor.capture());
    }

    @DisplayName("2라운드 - 웃었을 때")
    @Test
    void deSerializationCaptureTest_2Round_LAUGH() {
        String captureJson = createCaptureJson(2, gameRoomId, true, "Smile", 0.95, 0.95);

        CaptureResult captureResult = processCaptureResult(captureJson, 2, true);
        assertEquals("Smile", captureResult.getEmotion());
        assertEquals(0.95, captureResult.getProbability());
        assertEquals(0.95, captureResult.getSmileProbability());

        verifyCaptureResultEventPublished(captureResult);

        verify(publisher, never()).publishEvent(firstRoundOverEventCaptor.capture());
        verify(publisher).publishEvent(secondRoundOverEventCaptor.capture());
        SecondRoundOverEvent secondRoundOverEvent = secondRoundOverEventCaptor.getValue();
        assertThat(secondRoundOverEvent.gameRoomId()).isEqualTo(gameRoomId);
        assertThat(secondRoundOverEvent.roundResult()).isEqualTo(RoundResult.LAUGH);
    }

    @DisplayName("2라운드 - 안 웃었을 때")
    @Test
    void deSerializationCaptureTest_2Round_NO_LAUGH() {
        String captureJson = createCaptureJson(2, gameRoomId, true, "Neutral", 0.10, 0.10);

        CaptureResult captureResult = processCaptureResult(captureJson, 2, false);
        assertEquals("Neutral", captureResult.getEmotion());
        assertEquals(0.10, captureResult.getProbability());
        assertEquals(0.10, captureResult.getSmileProbability());

        verifyCaptureResultEventPublished(captureResult);

        verify(publisher, never()).publishEvent(firstRoundOverEventCaptor.capture());
        verify(publisher, never()).publishEvent(secondRoundOverEventCaptor.capture());
    }

    private String createCaptureJson(int round, String gameSessionId, boolean isSuccess, String emotion, double probability, double smileProbability) {
        // Creating the header part of the JSON
        String headerJson = String.format(
                "{\"round\": %d, \"gameSessionId\": \"%s\"}",
                round,
                gameSessionId
        );

        String imageJson = "";
        if ("Smile".equals(emotion) && probability >= 0.5) {
            imageJson = ",\"encodedImage\": \"AABS@9XX1234\"";
        }


        return String.format(
                "{" +
                        "\"header\": %s%s," +
                        "\"success\": %b," +
                        "\"emotion\": \"%s\"," +
                        "\"probability\": %.2f," +
                        "\"smileProbability\": %.2f" +
                        "}",
                headerJson,
                imageJson,
                isSuccess,
                emotion,
                probability,
                smileProbability
        );
    }




    // Other test methods follow, using the processCaptureResult and verifyCaptureResultEventPublished methods...
}
