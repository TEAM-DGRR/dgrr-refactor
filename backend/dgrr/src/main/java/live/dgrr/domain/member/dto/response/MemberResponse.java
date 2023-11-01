package live.dgrr.domain.member.dto.response;

import lombok.Builder;

@Builder
public record MemberResponse(
        Long memberId,
        String nickname,
        String profileImage,
        String description
) {
    public static MemberResponse of(Long memberId, String nickname, String profileImage, String description) {
        return MemberResponse.builder()
                .memberId(memberId)
                .nickname(nickname)
                .profileImage(profileImage)
                .description(description)
                .build();
    }
}
