package live.dgrr.domain.game.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.time.LocalDateTime;

@Getter
@RedisHash(value = "gameRoom", timeToLive = 200)
public class GameRoom {

    @Id
    private String gameRoomId;
    private GameMember memberOne;
    private GameMember memberTwo;
    private GameStatus gameStatus;
    private int firstRoundPrepareCounter;
    private LocalDateTime firstRoundStartTime;
    private LocalDateTime firstRoundEndTime;
    private int secondRoundPrepareCounter;
    private LocalDateTime secondRoundStartTime;
    private LocalDateTime secondRoundEndTime;
    private RoundResult firstRoundResult;
    private RoundResult secondRoundResult;

    public GameRoom(String gameRoomId, GameMember memberOne, GameMember memberTwo, GameStatus gameStatus) {
        this.gameRoomId = gameRoomId;
        this.memberOne = memberOne;
        this.memberTwo = memberTwo;
        this.gameStatus = gameStatus;
    }

    public int firstRoundPrepare() {
        this.firstRoundPrepareCounter++;
        return this.firstRoundPrepareCounter;
    }

    public void startFirstRound(LocalDateTime now) {
        this.firstRoundStartTime = now;
        this.gameStatus = GameStatus.FIRST_ROUND;
    }

    public void finishFirstRound(RoundResult roundResult) {
        this.firstRoundResult = roundResult;
        this.firstRoundEndTime = LocalDateTime.now();
        this.gameStatus = GameStatus.SECOND_ROUND;
    }

    public int secondRoundPrepare() {
        this.secondRoundPrepareCounter++;
        return this.secondRoundPrepareCounter;
    }

    public void startSecondRound(LocalDateTime now) {
        this.secondRoundStartTime = now;
    }

    public void finishSecondRound(RoundResult roundResult) {
        this.secondRoundResult = roundResult;
        this.secondRoundEndTime = LocalDateTime.now();
        this.gameStatus = GameStatus.DONE;
    }
}