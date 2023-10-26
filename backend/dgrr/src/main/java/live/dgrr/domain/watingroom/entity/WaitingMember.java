package live.dgrr.domain.watingroom.entity;

import lombok.Builder;

@Builder
public record WaitingMember (
        Long waitingMemberId,
        String nickname,
        String profileImage,
        boolean isReady
) {
    public static WaitingMember of(Long waitingMemberId, String nickname, String profileImage, boolean isReady) {
        return WaitingMember.builder()
                .waitingMemberId(waitingMemberId)
                .nickname(nickname)
                .profileImage(profileImage)
                .isReady(isReady)
                .build();

    }
}
