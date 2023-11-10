package live.dgrr.domain.game.service;

import live.dgrr.domain.capture.service.IHighlightService;
import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.entity.*;
import live.dgrr.domain.game.entity.event.*;
import live.dgrr.domain.game.repository.GamePrepareRepository;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.gamehistory.service.GameHistoryService;
import live.dgrr.domain.waitingroom.service.WaitingRoomService;
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
    private final GameHistoryService gameHistoryService;
    private final GamePrepareRepository gamePrepareRepository;
    private final WaitingRoomService waitingRoomService;
    private final IHighlightService iHighlightService;

    private static final long ROUND_TIME = 30L;
    private static final String SECOND_ROUND_LAUGH = "/recv/secondroundend-laugh";
    private static final String SECOND_ROUND_NO_LAUGH = "/recv/secondroundend-no-laugh";

    /**
     * 2라운드 준비 신호
     * @param gameRoomId
     */
    public void prepareSecondRoundStart(String gameRoomId) {

        Long prepareCounter = gamePrepareRepository.prepareRoundTwo(gameRoomId);

        //둘 모두 준비되었을때만 시작
        if(prepareCounter == 2) {
            GameRoom gameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));
            gamePrepareRepository.deletePrepareRoundTwo(gameRoomId);
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

        //highlight Image 가져오는 로직
        String highlightImage = null;
        Integer winRound = gameRoom.judgeWinningRound();
        if(winRound != null) {
            highlightImage = iHighlightService.highlightImage(gameRoomId, winRound);
        }

        int afterRating = EloCalculator.calculateRating(myInfo.rating(), enemyInfo.rating(), gameResult);
        Tier afterTier = TierCalculator.calculateRank(afterRating);

        int roomId = waitingRoomService.createWaitingRoom();

        //게임 결과 저장
        gameHistoryService.save(gameRoom, gameRoomId, memberId, gameResult, afterRating - myInfo.rating(), highlightImage);

        return GameResultResponse.builder()
                .gameResult(gameResult)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .highlightImage(highlightImage)
                .afterRating(afterRating)
                .afterTier(afterTier)
                .roomId(roomId)
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

        //todo: 게임 팅겼을 시 history 저장
//        gameHistoryService.save(gameRoom, gameRoomId, memberId, GameResult.WIN, afterRating - myInfo.rating(), null);

        return GameResultResponse.builder()
                .gameResult(GameResult.WIN)
                .myInfo(myInfo)
                .enemyInfo(enemyInfo)
                .afterRating(afterRating)
                .afterTier(afterTier)
                .build();
    }
}
