package live.dgrr.domain.member.dto.response;

import live.dgrr.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberResponse(
        Long memberId,
        String kakaoId,
        String nickname,
        String profileImage,
        String description
) {
    public static MemberResponse of(Long memberId, String kakaoId, String nickname, String profileImage, String description) {
        return MemberResponse.builder()
                .memberId(memberId)
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .description(description)
                .build();
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .kakaoId(member.getKakaoId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .description(member.getDescription())
                .build();
    }
}
