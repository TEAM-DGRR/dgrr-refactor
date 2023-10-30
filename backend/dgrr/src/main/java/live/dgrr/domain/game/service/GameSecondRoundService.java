package live.dgrr.domain.game.service;

import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.entity.*;
import live.dgrr.domain.game.entity.event.*;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.entity.Rank;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import live.dgrr.global.util.RankCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class GameSecondRoundService {

    private final GameRoomRepository gameRoomRepository;
    private final ApplicationEventPublisher publisher;

    private static final long ROUND_TIME = 5000L;
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

    private void secondRoundStart(String gameRoomId, GameRoom gameRoom) {
        LocalDateTime now = LocalDateTime.now();

        //todo: timer 추후 변경 필요
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                publisher.publishEvent(new SecondRoundOverEvent(gameRoomId, RoundResult.NO_LAUGH));
            }
        };

        gameRoom.startSecondRound(now);
        gameRoomRepository.save(gameRoom);
        timer.schedule(timerTask,ROUND_TIME);

        publisher.publishEvent(new SecondRoundPreparedEvent(gameRoom.getMemberOne().memberId(), gameRoom.getMemberTwo().memberId()));
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
                gameRoom.getMemberTwo().memberId(), gameRoom.getFirstRoundResult(), destination));
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
        //todo: rating 시스템 적용
        int afterRating = myInfo.rating() + 20;
        Rank afterRank = RankCalculator.calculateRank(afterRating);

        return GameResultResponse.builder()
                .gameResult(gameResult)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .highlightImage(null)
                .afterRating(afterRating)
                .afterRank(afterRank)
                .build();
    }

    public GameResultResponse leaveGame(String memberId, String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

        GameMember myInfo = gameRoom.getEnemyInfo(memberId);
        GameMember enemyInfo = gameRoom.getMyInfo(memberId);
        //todo: rating 시스템 적용
        int afterRating = myInfo.rating() + 20;
        Rank afterRank = RankCalculator.calculateRank(afterRating);

        return GameResultResponse.builder()
                .gameResult(GameResult.WIN)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .afterRating(afterRating)
                .afterRank(afterRank)
                .build();
    }
}
