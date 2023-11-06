package live.dgrr.domain.game.service;


import live.dgrr.domain.game.dto.GameStartResponse;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundEndEvent;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.entity.MemberRole;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import live.dgrr.domain.ranking.repository.RankingRepository;
import live.dgrr.global.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameFirstRoundServiceTest {

    @Spy
    MemberRepository memberRepository;
    @Mock
    OpenviduService openviduService;
    @Mock
    RankingRepository rankingRepository;
    @Mock
    TaskScheduler taskScheduler;
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

    String gameRoomId;
    String memberOneId;
    String memberTwoId;

    GameMember memberOne;
    GameMember memberTwo;
    GameRoom gameRoom;

    @BeforeEach
    void setUp() {
        gameRoomId = "roomId";
        memberOneId = "5";
        memberTwoId = "6";


        memberOne = new GameMember(memberOneId, "Player1", "jpg", "des", 1500, Tier.SILVER);
        memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "des", 1200, Tier.BRONZE);
        gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START, GameType.RANDOM);
    }
    @DisplayName("게임 시작 테스트")
    @Test
    void gameStartTest() {
        Member mOne = new Member(Long.parseLong(memberOneId), "kakao1", "Player1", "jpg", "des", MemberRole.USER);
        Member mTwo = new Member(Long.parseLong(memberTwoId), "kakao1", "Player1", "jpg", "des", MemberRole.USER);

        doAnswer(invocation -> Optional.of(mOne)).when(memberRepository).findById(Long.parseLong(memberOneId));
        doAnswer(invocation -> Optional.of(mTwo)).when(memberRepository).findById(Long.parseLong(memberTwoId));

        List<GameStartResponse> gameStartResponses = gameFirstRoundService.gameStart(memberOneId, memberTwoId, GameType.RANDOM);
        assertThat(gameStartResponses.get(0).myInfo().memberId()).isEqualTo(memberOneId);
        assertThat(gameStartResponses.get(1).myInfo().memberId()).isEqualTo(memberTwoId);
    }

    @Test
    void firstRoundStart() {
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

        doAnswer(invocation -> Optional.of(gameRoom)).when(gameRoomRepository).findById(gameRoomId);

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

        doAnswer(invocation -> Optional.of(gameRoom)).when(gameRoomRepository).findById(gameRoomId);

        //when
        gameFirstRoundService.firstRoundOver(new FirstRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));

        //then
        verify(publisher).publishEvent(firstRoundEndEventCaptor.capture());
        FirstRoundEndEvent event = firstRoundEndEventCaptor.getValue();

        assertThat(event.memberOneId()).isEqualTo(memberOneId);
        assertThat(event.roundResult()).isEqualTo(RoundResult.NO_LAUGH);
    }
}