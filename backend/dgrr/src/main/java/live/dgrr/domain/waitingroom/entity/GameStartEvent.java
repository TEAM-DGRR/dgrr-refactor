package live.dgrr.domain.waitingroom.entity;

import live.dgrr.domain.game.entity.event.GameType;

public record GameStartEvent(String memberOneId, String memberTwoId, GameType gameType) {
}