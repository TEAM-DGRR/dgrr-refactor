package live.dgrr.domain.waitingroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingMember {
    private Long waitingMemberId;
    private String nickname;
    private String profileImage;
    private boolean isReady;

    public static WaitingMember of(Long waitingMemberId, String nickname, String profileImage, boolean isReady) {
        return WaitingMember.builder()
                .waitingMemberId(waitingMemberId)
                .nickname(nickname)
                .profileImage(profileImage)
                .isReady(isReady)
                .build();
    }

    public void toggleReady() {
        this.isReady = !this.isReady;
    }
}
