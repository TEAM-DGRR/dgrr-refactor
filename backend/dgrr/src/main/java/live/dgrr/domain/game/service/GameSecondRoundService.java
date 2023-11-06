package live.dgrr.domain.game.service;

import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.entity.*;
import live.dgrr.domain.game.entity.event.*;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.gamehistory.entity.GameHistory;
import live.dgrr.domain.gamehistory.repository.GameHistoryRepository;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.global.entity.Tier;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import live.dgrr.global.util.EloCalculator;
import live.dgrr.global.util.TierCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class GameSecondRoundService {

    private final GameRoomRepository gameRoomRepository;
    private final ApplicationEventPublisher publisher;
    private final TaskScheduler taskScheduler;
    private final MemberRepository memberRepository;
    private final GameHistoryRepository gameHistoryRepository;

    private static final long ROUND_TIME = 30L;
    private static final String SECOND_ROUND_LAUGH = "/recv/secondroundend-laugh";
    private static final String SECOND_ROUND_NO_LAUGH = "/recv/secondroundend-no-laugh";

    /**
     * 2라운드 준비 신호
     * @param gameRoomId
     */
    public void prepareSecondRoundStart(String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));
        int prepareCounter = gameRoom.secondRoundPrepare();
        gameRoomRepository.save(gameRoom);

        //todo: 동시성 이슈 처리 필요
        //둘 모두 준비되었을때만 시작
        if(prepareCounter == 2) {
            secondRoundStart(gameRoomId, gameRoom);
        }
    }

    public Instant secondRoundStart(String gameRoomId, GameRoom gameRoom) {
        Instant now = Instant.now();

        taskScheduler.schedule(() -> {
            publisher.publishEvent(new SecondRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));
        }, now.plusSeconds(ROUND_TIME));

        gameRoom.startSecondRound(now.atZone(ZoneId.systemDefault()).toLocalDateTime());
        gameRoomRepository.save(gameRoom);

        publisher.publishEvent(new SecondRoundPreparedEvent(gameRoom.getMemberOne().memberId(), gameRoom.getMemberTwo().memberId()));
        return now;
    }

    /**
     * 2 라운드 종료 메소드
     * @param event 2라운드 종료 정보 담긴 이벤트
     */
    @EventListener
    public void secondRoundOver(SecondRoundOverEvent event) {
        GameRoom gameRoom = gameRoomRepository.findById(event.gameRoomId())
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

        //이미 웃어서 라운드 종료 된경우
        if(gameRoom.getGameStatus().equals(GameStatus.DONE)) {
            return;
        }

        gameRoom.finishSecondRound(event.roundResult());
        gameRoomRepository.save(gameRoom);

        String destination;
        if(event.roundResult().equals(RoundResult.LAUGH)) {
            destination = SECOND_ROUND_LAUGH;
        }
        else {
            destination = SECOND_ROUND_NO_LAUGH;
        }

        publisher.publishEvent(new SecondRoundEndEvent(gameRoom.getMemberOne().memberId(),
                gameRoom.getMemberTwo().memberId(), gameRoom.getSecondRoundResult(), destination));
    }

    /**
     * 게임 결과 반환
     * @param memberId
     * @param gameRoomId
     */
    public GameResultResponse gameResult(String memberId, String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

        GameResult gameResult = gameRoom.judgeResult(memberId);
        GameMember myInfo = gameRoom.getMyInfo(memberId);
        GameMember enemyInfo = gameRoom.getEnemyInfo(memberId);
        //todo: highlightImage 가져오는 로직 필요
        int afterRating = EloCalculator.calculateRating(myInfo.rating(), enemyInfo.rating(), gameResult);
        Tier afterTier = TierCalculator.calculateRank(afterRating);

        //게임 결과 저장
        Member member = memberRepository.findById(Long.parseLong(myInfo.memberId()))
                .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

        GameHistory history = GameHistory.builder()
                .member(member)
                .gameRoomId(gameRoomId)
                .gameResult(gameResult)
                .gameType(gameRoom.getGameType())
                .gameTime(gameRoom.getGameTime())
                .ratingChange(afterRating - myInfo.rating())
                .holdingTime(gameRoom.getHoldingTime(memberId))
                .highlightImage(null)
                .build();

        gameHistoryRepository.save(history);


        return GameResultResponse.builder()
                .gameResult(gameResult)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .highlightImage(null)
                .afterRating(afterRating)
                .afterTier(afterTier)
                .build();
    }

    /**
     * 상대방 탈주시 실행 메소드
     * @param memberId
     * @param gameRoomId
     * @return
     */
    public GameResultResponse leaveGame(String memberId, String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

        GameMember myInfo = gameRoom.getEnemyInfo(memberId);
        GameMember enemyInfo = gameRoom.getMyInfo(memberId);
        int afterRating = EloCalculator.calculateRating(myInfo.rating(), enemyInfo.rating(), GameResult.WIN);
        Tier afterTier = TierCalculator.calculateRank(afterRating);

        Member member = memberRepository.findById(Long.parseLong(myInfo.memberId()))
                .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

        //게임 결과 저장
        GameHistory history = GameHistory.builder()
                .member(member)
                .gameRoomId(gameRoomId)
                .gameResult(GameResult.WIN)
                .gameType(gameRoom.getGameType())
                .gameTime(gameRoom.getGameTime())
                .ratingChange(afterRating - myInfo.rating())
                .holdingTime(gameRoom.getHoldingTime(memberId))
                .highlightImage(null)
                .build();

        gameHistoryRepository.save(history);

        return GameResultResponse.builder()
                .gameResult(GameResult.WIN)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .afterRating(afterRating)
                .afterTier(afterTier)
                .build();
    }
}
