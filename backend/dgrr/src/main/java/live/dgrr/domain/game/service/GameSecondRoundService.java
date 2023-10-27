package live.dgrr.domain.game.service;

import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.entity.event.SecondRoundEndEvent;
import live.dgrr.domain.game.entity.event.SecondRoundPreparedEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
                publisher.publishEvent(new SecondRoundEndEvent(gameRoomId, RoundResult.NO_LAUGH));
            }
        };

        gameRoom.startSecondRound(now);
        gameRoomRepository.save(gameRoom);
        timer.schedule(timerTask,ROUND_TIME);

        publisher.publishEvent(new SecondRoundPreparedEvent(gameRoom.getMemberOne().memberId(), gameRoom.getMemberTwo().memberId()));
    }
}