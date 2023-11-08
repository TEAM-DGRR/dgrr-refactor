package live.dgrr.domain.game.service;


import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.entity.*;
import live.dgrr.domain.game.entity.event.*;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.gamehistory.service.GameHistoryService;
import live.dgrr.domain.waitingroom.service.WaitingRoomService;
import live.dgrr.global.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameSecondRoundServiceTest {

    @Mock
    WaitingRoomService waitingRoomService;
    @Mock
    GameHistoryService gameHistoryService;
    @Spy
    GameRoomRepository gameRoomRepository;
    @Spy
    ApplicationEventPublisher publisher;
    @InjectMocks
    GameSecondRoundService gameSecondRoundService;
    @Mock
    TaskScheduler taskScheduler;
    @Captor
    private ArgumentCaptor<SecondRoundPreparedEvent> secondRoundPreparedEventCaptor;

    @Captor
    private ArgumentCaptor<SecondRoundEndEvent> secondRoundEndEventCaptor;

    String gameRoomId;
    String memberOneId;
    String memberTwoId;

    GameMember memberOne;
    GameMember memberTwo;
    GameRoom gameRoom;

    @BeforeEach
    void setUp() {
        gameRoomId = "roomId";
        memberOneId = "6";
        memberTwoId = "5";

        memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Tier.SILVER);
        memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Tier.BRONZE);
        gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START, GameType.RANDOM);
    }

    @Test
    void 이라운드_시작() {

        //when
        gameSecondRoundService.secondRoundStart(gameRoomId,gameRoom);

        //then
        verify(publisher).publishEvent(secondRoundPreparedEventCaptor.capture());
        SecondRoundPreparedEvent capturedEvent = secondRoundPreparedEventCaptor.getValue();

        assertThat(capturedEvent.memberOneId()).isEqualTo(memberOneId);
        assertThat(capturedEvent.memberTwoId()).isEqualTo(memberTwoId);
    }

    @Test
    void 웃은경우_이라운드_종료() {
        //given
        doAnswer(invocation -> Optional.of(gameRoom)).when(gameRoomRepository).findById(gameRoomId);

        //when
        gameSecondRoundService.secondRoundOver(new SecondRoundOverEvent(gameRoomId, RoundResult.LAUGH));

        //then
        verify(publisher).publishEvent(secondRoundEndEventCaptor.capture());
        SecondRoundEndEvent event = secondRoundEndEventCaptor.getValue();

        assertThat(event.memberOneId()).isEqualTo(memberOneId);
        assertThat(event.roundResult()).isEqualTo(RoundResult.LAUGH);
    }

    @Test
    void 안웃은경우_이라운드_종료() {
        //given
        doAnswer(invocation -> Optional.of(gameRoom)).when(gameRoomRepository).findById(gameRoomId);

        //when
        gameSecondRoundService.secondRoundOver(new SecondRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));

        //then
        verify(publisher).publishEvent(secondRoundEndEventCaptor.capture());
        SecondRoundEndEvent event = secondRoundEndEventCaptor.getValue();

        assertThat(event.memberOneId()).isEqualTo(memberOneId);
        assertThat(event.roundResult()).isEqualTo(RoundResult.NO_LAUGH);
    }

    @Test
    void 둘다_안웃은경우_비김() {
        //given
        mockGameRoom(RoundResult.NO_LAUGH, RoundResult.NO_LAUGH);
        //when

        GameResultResponse result = gameSecondRoundService.gameResult(memberOneId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.DRAW);
    }

    @Test
    void 일라운드만_웃은경우_멤버원_승리() {
        //given
        mockGameRoom(RoundResult.LAUGH, RoundResult.NO_LAUGH);
        //when

        GameResultResponse result = gameSecondRoundService.gameResult(memberOneId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.WIN);
    }

    @Test
    void 일라운드만_웃은경우_멤버투_패배() {
        //given
        mockGameRoom(RoundResult.LAUGH, RoundResult.NO_LAUGH);
        //when

        GameResultResponse result = gameSecondRoundService.gameResult(memberTwoId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.LOSE);
    }

    @Test
    void 이라운드만_웃은경우_멤버원_패배() {
        //given
        mockGameRoom(RoundResult.NO_LAUGH, RoundResult.LAUGH);
        //when

        GameResultResponse result = gameSecondRoundService.gameResult(memberOneId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.LOSE);
    }

    @Test
    void 이라운드만_웃은경우_멤버투_승리() {
        //given
        mockGameRoom(RoundResult.NO_LAUGH, RoundResult.LAUGH);
        //when

        GameResultResponse result = gameSecondRoundService.gameResult(memberTwoId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.WIN);
    }

    @Test
    void 상대방_탈주() {
        //given
        doAnswer(invocation -> Optional.of(gameRoom)).when(gameRoomRepository).findById(gameRoomId);

        //when
        GameResultResponse result = gameSecondRoundService.leaveGame(memberOneId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.WIN);
        assertThat(result.myInfo().memberId()).isEqualTo(memberTwoId);
    }

    private void mockGameRoom(RoundResult firstRoundResult, RoundResult secondRoundResult) {
        doAnswer(invocation -> {
            gameRoom.finishFirstRound(firstRoundResult);
            gameRoom.finishSecondRound(secondRoundResult);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);
    }
}