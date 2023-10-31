package live.dgrr.domain.game.service;


import live.dgrr.domain.game.dto.GameStartResponse;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundEndEvent;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import live.dgrr.global.entity.Rank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameFirstRoundServiceTest {

    @Mock
    OpenviduService openviduService;
    @Spy
    GameRoomRepository gameRoomRepository;
    @Spy
    ApplicationEventPublisher publisher;
    @Captor
    private ArgumentCaptor<FirstRoundPreparedEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<FirstRoundEndEvent> firstRoundEndEventCaptor;

    @InjectMocks
    GameFirstRoundService gameFirstRoundService;
    @Test
    void gameStartTest() {
        String memberOneId = "oneId";
        String memberTwoId = "twoId";
        List<GameStartResponse> gameStartResponses = gameFirstRoundService.gameStart(memberOneId, memberTwoId);
        assertThat(gameStartResponses.get(0).myInfo().memberId()).isEqualTo(memberOneId);
        assertThat(gameStartResponses.get(1).myInfo().memberId()).isEqualTo(memberTwoId);
    }

    @Test
    void firstRoundStart() {
        //given
        String gameRoomId = "roomId";
        String memberOneId = "M1";
        String memberTwoId = "M2";

        GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
        GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
        GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START);

        //when
        gameFirstRoundService.firstRoundStart(gameRoomId,gameRoom);

        //then
        verify(publisher).publishEvent(eventCaptor.capture());
        FirstRoundPreparedEvent capturedEvent = eventCaptor.getValue();

        assertThat(capturedEvent.memberOneId()).isEqualTo(memberOneId);
        assertThat(capturedEvent.memberTwoId()).isEqualTo(memberTwoId);
    }

    @Test
    void 웃은경우_첫라운드_종료() {

        //given
        String gameRoomId = "id";
        String memberOneId = "M1";
        String memberTwoId = "M2";
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);

        //when
        gameFirstRoundService.firstRoundOver(new FirstRoundOverEvent(gameRoomId, RoundResult.LAUGH));

        //then
        verify(publisher).publishEvent(firstRoundEndEventCaptor.capture());
        FirstRoundEndEvent event = firstRoundEndEventCaptor.getValue();

        assertThat(event.memberOneId()).isEqualTo(memberOneId);
        assertThat(event.roundResult()).isEqualTo(RoundResult.LAUGH);
    }

    @Test
    void 안웃은경우_첫라운드_종료() {

        //given
        String gameRoomId = "id";
        String memberOneId = "M1";
        String memberTwoId = "M2";
        doAnswer(invocation -> {
            GameMember memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Rank.SILVER);
            GameMember memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Rank.BRONZE);
            GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.FIRST_ROUND);
            return Optional.of(gameRoom);
        }).when(gameRoomRepository).findById(gameRoomId);

        //when
        gameFirstRoundService.firstRoundOver(new FirstRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));

        //then
        verify(publisher).publishEvent(firstRoundEndEventCaptor.capture());
        FirstRoundEndEvent event = firstRoundEndEventCaptor.getValue();

        assertThat(event.memberOneId()).isEqualTo(memberOneId);
        assertThat(event.roundResult()).isEqualTo(RoundResult.NO_LAUGH);
    }
}