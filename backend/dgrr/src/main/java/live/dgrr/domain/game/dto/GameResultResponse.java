package live.dgrr.domain.game.dto;

import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameResult;
import live.dgrr.global.entity.Rank;


public record GameResultResponse(GameMember myInfo, GameMember enemyInfo, String highlightImage, GameResult gameResult,
                                 int afterRating, Rank afterRank)  {
}
