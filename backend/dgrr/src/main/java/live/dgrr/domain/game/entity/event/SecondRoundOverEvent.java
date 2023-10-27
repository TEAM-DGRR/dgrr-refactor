package live.dgrr.domain.game.entity.event;

import live.dgrr.domain.game.entity.RoundResult;

public record SecondRoundOverEvent(String gameRoomId, RoundResult roundResult) {
}
