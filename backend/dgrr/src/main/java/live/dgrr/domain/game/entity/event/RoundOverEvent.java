package live.dgrr.domain.game.entity.event;

import live.dgrr.domain.game.entity.GameStatus;

public record RoundOverEvent(String gameRoomId, GameStatus gameStatus) {

}
