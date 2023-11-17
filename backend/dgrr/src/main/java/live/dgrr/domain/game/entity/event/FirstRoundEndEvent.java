package live.dgrr.domain.game.entity.event;

import live.dgrr.domain.game.entity.RoundResult;

public record FirstRoundEndEvent(String memberOneId, String memberTwoId, RoundResult roundResult, String destination) {
}
