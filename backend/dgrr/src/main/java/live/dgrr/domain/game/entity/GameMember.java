package live.dgrr.domain.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.global.entity.Tier;


public record GameMember(@JsonIgnore String memberId, String nickname, String profileImage, String description, int rating, Tier tier) {
    public GameMember(Member member, int rating, Tier tier) {
        this(String.valueOf(member.getMemberId()), member.getNickname(), member.getProfileImage(), member.getDescription(), rating, tier);
    }
}
