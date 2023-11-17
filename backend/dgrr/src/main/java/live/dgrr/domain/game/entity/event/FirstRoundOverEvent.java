package live.dgrr.domain.game.entity.event;

import live.dgrr.domain.game.entity.RoundResult;

public record FirstRoundOverEvent(String gameRoomId, RoundResult roundResult) {

}
