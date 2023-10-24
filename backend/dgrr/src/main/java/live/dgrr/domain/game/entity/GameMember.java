package live.dgrr.domain.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import live.dgrr.global.entity.Rank;


public record GameMember(@JsonIgnore String memberId, String nickname, String profileImage, String description, int rating, Rank rank) {
}
