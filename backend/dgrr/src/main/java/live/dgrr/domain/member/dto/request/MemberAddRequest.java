package live.dgrr.domain.member.dto.request;

import jakarta.validation.constraints.Size;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.entity.MemberRole;

public record MemberAddRequest (
        String kakaoId,
        @Size(min = 1, max = 12, message = "Validation Nickname Length Invalid")
        String nickname,
        String profileImage,
        @Size(max = 20, message = "Validation Description Length Invalid")
        String description,
        MemberRole memberRole
) {
    public Member forAddMember() {
        Member member = Member.builder()
                .kakaoId(this.kakaoId)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .description(this.description)
                .memberRole(this.memberRole)
                .build();
        return member;
    }
}
