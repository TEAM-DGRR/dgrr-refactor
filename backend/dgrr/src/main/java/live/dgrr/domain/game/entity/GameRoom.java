package live.dgrr.domain.game.entity;

import live.dgrr.domain.game.entity.event.GameType;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@RedisHash(value = "gameRoom", timeToLive = 200)
public class GameRoom {

    @Id
    private String gameRoomId;
    private GameType gameType;
    private GameMember memberOne;
    private GameMember memberTwo;
    private GameStatus gameStatus;
    private LocalDateTime firstRoundStartTime;
    private LocalDateTime firstRoundEndTime;
    private LocalDateTime secondRoundStartTime;
    private LocalDateTime secondRoundEndTime;
    private RoundResult firstRoundResult;
    private RoundResult secondRoundResult;

    public GameRoom(String gameRoomId, GameMember memberOne, GameMember memberTwo, GameStatus gameStatus, GameType gameType) {
        this.gameRoomId = gameRoomId;
        this.memberOne = memberOne;
        this.memberTwo = memberTwo;
        this.gameStatus = gameStatus;
        this.gameType = gameType;
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

    public void startSecondRound(LocalDateTime now) {
        this.secondRoundStartTime = now;
    }

    public void finishSecondRound(RoundResult roundResult) {
        this.secondRoundResult = roundResult;
        this.secondRoundEndTime = LocalDateTime.now();
        this.gameStatus = GameStatus.DONE;
    }

    public GameResult judgeResult(String memberId) {

        //두명 모두 안 웃은 경우 DRAW
        if(firstRoundResult.equals(RoundResult.NO_LAUGH) && secondRoundResult.equals(RoundResult.NO_LAUGH)) {
            return GameResult.DRAW;
        }

        //두명 다 웃은 경우
        if(firstRoundResult.equals(RoundResult.LAUGH) && secondRoundResult.equals(RoundResult.LAUGH)) {
            long firstRoundDuration = Duration.between(firstRoundStartTime, firstRoundEndTime).toNanos();
            long secondRoundDuration = Duration.between(secondRoundStartTime, secondRoundEndTime).toNanos();

            // 1라운드 공격자 승리
            if(firstRoundDuration < secondRoundDuration) {
                return judgeWinLose(memberId, memberOne);
            }
            // 2라운드 공격자 승리
            if(secondRoundDuration < firstRoundDuration) {
                return judgeWinLose(memberId, memberTwo);
            }
        }
        //1라운드에만 웃은 경우
        else if(firstRoundResult.equals(RoundResult.LAUGH)) {
            return judgeWinLose(memberId, memberOne);
        }
        //2라운드에만 웃은경우
        else if(secondRoundResult.equals(RoundResult.LAUGH)){
            return judgeWinLose(memberId, memberTwo);
        }
        return null;
    }

    private GameResult judgeWinLose(String memberId, GameMember winner) {
        if (winner.memberId().equals(memberId)) {
            return GameResult.WIN;
        } else {
            return GameResult.LOSE;
        }
    }

    public GameMember getMyInfo(String memberId) {
        if(memberOne.memberId().equals(memberId)) {
            return memberOne;
        }
        else {
            return memberTwo;
        }
    }
    public GameMember getEnemyInfo(String memberId) {
        if(memberOne.memberId().equals(memberId)) {
            return memberTwo;
        }
        else {
            return memberOne;
        }
    }

    public long getGameTime() {
        long firstRoundDuration = Duration.between(firstRoundStartTime, firstRoundEndTime).toMillis();
        long secondRoundDuration = Duration.between(secondRoundStartTime, secondRoundEndTime).toMillis();
        return firstRoundDuration + secondRoundDuration;
    }

    public long getHoldingTime(String memberId) {
        if(memberId.equals(memberOne.memberId())) {
            return Duration.between(secondRoundStartTime, secondRoundEndTime).toMillis();
        }
        return Duration.between(firstRoundStartTime, firstRoundEndTime).toMillis();
    }

    public Integer judgeWinningRound() {
        if(firstRoundResult.equals(RoundResult.NO_LAUGH) && secondRoundResult.equals(RoundResult.NO_LAUGH)) {
            return null;
        }
        long firstRoundDuration = Duration.between(firstRoundStartTime, firstRoundEndTime).toNanos();
        long secondRoundDuration = Duration.between(secondRoundStartTime, secondRoundEndTime).toNanos();

        // 1라운드가 더 빨리 웃엇을 경우
        if(firstRoundDuration < secondRoundDuration) {
            return 1;
        }
        // 2라운드가 더 빨리 웃엇을 경우
        return 2;
    }
}