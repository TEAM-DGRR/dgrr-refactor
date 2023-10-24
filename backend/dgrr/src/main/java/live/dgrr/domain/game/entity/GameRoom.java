package live.dgrr.domain.game.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.time.LocalDateTime;

@RedisHash(value = "gameRoom", timeToLive = 200)
public class GameRoom {

    @Id
    private String gameRoomId;
    private GameMember memberOne;
    private GameMember memberTwo;
    private GameStatus gameStatus;
    private LocalDateTime firstRoundStartTime;
    private LocalDateTime firstRoundEndTime;
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
}