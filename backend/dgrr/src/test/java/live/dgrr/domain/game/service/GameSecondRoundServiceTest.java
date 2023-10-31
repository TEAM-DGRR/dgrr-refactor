package live.dgrr.domain.game.service;


import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.entity.*;
import live.dgrr.domain.game.entity.event.*;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.entity.Rank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameSecondRoundServiceTest {

    @Spy
    GameRoomRepository gameRoomRepository;
    @Spy
    ApplicationEventPublisher publisher;
    @InjectMocks
    GameSecondRoundService gameSecondRoundService;
    @Captor
    private ArgumentCaptor<SecondRoundPreparedEvent> secondRoundPreparedEventCaptor;

    @Captor
    private ArgumentCaptor<SecondRoundEndEvent> secondRoundEndEventCaptor;

    String gameRoomId = "roomId";
    String memberOneId = "M1";
    String memberTwoId = "M2";

    @Test
    void 이라운드_시작() {
        //given
        GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
        GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
        GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START);

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
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);

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
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);

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
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);

        //when
        GameResultResponse result = gameSecondRoundService.leaveGame(memberOneId, gameRoomId);

        //then
        assertThat(result.gameResult()).isEqualTo(GameResult.WIN);
        assertThat(result.myInfo().memberId()).isEqualTo(memberTwoId);
    }

    private void mockGameRoom(RoundResult firstRoundResult, RoundResult secondRoundResult) {
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1400, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1400, Rank.SILVER);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            gameRoom.finishFirstRound(firstRoundResult);
            gameRoom.finishSecondRound(secondRoundResult);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);
    }
}