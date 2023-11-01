package live.dgrr.domain.gamehistory.dto.response;

import live.dgrr.domain.game.entity.GameResult;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.gamehistory.entity.GameHistory;
import live.dgrr.domain.member.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GameHistoryWithOpponentInfoResponse(
        Long gameHistoryId,
        GameResult gameResult,
        GameType gameType,
        Integer holdingTime,
        Integer ratingChange,
        String highlightImage,
        LocalDateTime createdAt,
        String opponentNickname,
        String opponentProfileImage,
        String opponentDescription

) {
    public static GameHistoryWithOpponentInfoResponse of(GameHistory gameHistory, Member member
                                                            ) {
       return GameHistoryWithOpponentInfoResponse.builder()
               .gameHistoryId(gameHistory.getGameHistoryId())
               .gameResult(gameHistory.getGameResult())
               .gameType(gameHistory.getGameType())
               .holdingTime(gameHistory.getHoldingTime())
               .ratingChange(gameHistory.getRatingChange())
               .highlightImage(gameHistory.getHighlightImage())
               .createdAt(gameHistory.getCreatedAt())
               .opponentNickname(member.getNickname())
               .opponentProfileImage(member.getProfileImage())
               .opponentDescription(member.getDescription())
               .build();
    }
}