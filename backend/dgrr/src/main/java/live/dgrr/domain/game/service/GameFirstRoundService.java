package live.dgrr.domain.game.service;

import live.dgrr.domain.game.dto.GameStartResponse;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundEndEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import live.dgrr.domain.ranking.repository.RankingRepository;
import live.dgrr.global.entity.Tier;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import live.dgrr.global.util.TierCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameFirstRoundService {

    private final OpenviduService openviduService;
    private final GameRoomRepository gameRoomRepository;
    private final ApplicationEventPublisher publisher;
    private final TaskScheduler taskScheduler;
    private final MemberRepository memberRepository;
    private final RankingRepository rankingRepository;

    private static final long ROUND_TIME = 30L;

    private static final String FIRST_ROUND_LAUGH = "/recv/firstroundend-laugh";
    private static final String FIRST_ROUND_NO_LAUGH = "/recv/firstroundend-no-laugh";

    /**
     *  게임 시작 시 동작하는 메소드
     * @param memberOneId 멤버1아이디
     * @param memberTwoId 멤버2 아이디
     */
    public List<GameStartResponse> gameStart(String memberOneId, String memberTwoId, GameType gameType) {
        GameMember memberOne = makeGameMember(Long.parseLong(memberOneId));
        GameMember memberTwo = makeGameMember(Long.parseLong(memberTwoId));

        //게임 ID 생성
        String gameRoomId = UUID.randomUUID().toString();

        //오픈비두 세션 생성, 토큰 생성
        String openviduSessionId = openviduService.createSession(gameRoomId);
        String openviduTokenOne = openviduService.createConnection(openviduSessionId);
        String openviduTokenTwo = openviduService.createConnection(openviduSessionId);

        //Redis 에 게임 객체 저장
        GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START, gameType);
        gameRoomRepository.save(gameRoom);

        //GameStart 정보 전송
        GameStartResponse memberOneGameStartResponse = new GameStartResponse(memberOne,memberTwo,gameRoomId,openviduTokenOne,"FIRST");
        GameStartResponse memberTwoGameStartResponse = new GameStartResponse(memberTwo,memberOne,gameRoomId,openviduTokenTwo,"SECOND");

        return List.of(memberOneGameStartResponse, memberTwoGameStartResponse);
    }

    /**
     * 1라운드 준비 신호
     * @param gameRoomId 게임 룸 아이디
     */
    public void prepareFirstRoundStart(String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));
        int prepareCounter = gameRoom.firstRoundPrepare();
        gameRoomRepository.save(gameRoom);

        //todo: 동시성 이슈 처리 필요
        //둘 모두 준비되었을때만 시작
        if(prepareCounter == 2) {
            firstRoundStart(gameRoomId, gameRoom);
        }
    }

    /**
     * 1라운드 시작을 알리는 메소드
     * @param gameRoomId 게임 룸 아이디
     * @param gameRoom 게임룸 객체
     */
    public Instant firstRoundStart(String gameRoomId, GameRoom gameRoom) {
        Instant now = Instant.now();

        taskScheduler.schedule(() -> {
            publisher.publishEvent(new FirstRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));
        }, now.plusSeconds(ROUND_TIME));

        gameRoom.startFirstRound(now.atZone(ZoneId.systemDefault()).toLocalDateTime());
        gameRoomRepository.save(gameRoom);

        publisher.publishEvent(new FirstRoundPreparedEvent(gameRoom.getMemberOne().memberId(), gameRoom.getMemberTwo().memberId()));
        return now;
    }

    /**
     * 1 라운드 종료 메소드
     * @param event 1라운드 종료 정보 담긴 이벤트
     */
    @EventListener
    public void firstRoundOver(FirstRoundOverEvent event) {
        GameRoom gameRoom = gameRoomRepository.findById(event.gameRoomId())
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

        //이미 웃어서 라운드 종료 된경우
        if(gameRoom.getGameStatus().equals(GameStatus.SECOND_ROUND)) {
            return;
        }

        gameRoom.finishFirstRound(event.roundResult());
        gameRoomRepository.save(gameRoom);

        String destination;
        if(event.roundResult().equals(RoundResult.LAUGH)) {
            destination = FIRST_ROUND_LAUGH;
        }
        else {
            destination = FIRST_ROUND_NO_LAUGH;
        }

        publisher.publishEvent(new FirstRoundEndEvent(gameRoom.getMemberOne().memberId(),
                gameRoom.getMemberTwo().memberId(), gameRoom.getFirstRoundResult(), destination));
    }

    private GameMember makeGameMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));
        int rating = rankingRepository.getRatingByMemberId(memberId).intValue();
        Tier tier = TierCalculator.calculateRank(rating);
        return new GameMember(member,rating,tier);
    }
}
