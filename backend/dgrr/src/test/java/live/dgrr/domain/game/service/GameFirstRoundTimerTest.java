package live.dgrr.domain.game.service;


import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import live.dgrr.global.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameFirstRoundTimerTest {

    int ROUND_TIME = 30;

    @Mock
    TaskScheduler taskScheduler;
    @Mock
    OpenviduService openviduService;
    @Mock
    GameRoomRepository gameRoomRepository;
    @Mock
    ApplicationEventPublisher publisher;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Captor
    private ArgumentCaptor<Instant> instantCaptor;

    @InjectMocks
    private GameFirstRoundService gameFirstRoundService;

    String gameRoomId;
    String memberOneId;
    String memberTwoId;

    GameMember memberOne;
    GameMember memberTwo;
    GameRoom gameRoom;

    @BeforeEach
    void setUp() {
        gameRoomId = "roomId";
        memberOneId = "M1";
        memberTwoId = "M2";

        memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Tier.SILVER);
        memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Tier.BRONZE);
        gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START, GameType.RANDOM);
    }
    @Test
    void 첫라운드_타이머() {
        Instant startTime = gameFirstRoundService.firstRoundStart(gameRoomId, gameRoom);

        //then
        verify(taskScheduler, times(1)).schedule(runnableCaptor.capture(), instantCaptor.capture());

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(publisher, times(1)).publishEvent(any(FirstRoundOverEvent.class));

        Instant scheduledInstant = instantCaptor.getValue();

        Duration duration = Duration.between(startTime, scheduledInstant);
        assertThat(duration).isEqualTo(Duration.ofSeconds(ROUND_TIME));
    }
}
