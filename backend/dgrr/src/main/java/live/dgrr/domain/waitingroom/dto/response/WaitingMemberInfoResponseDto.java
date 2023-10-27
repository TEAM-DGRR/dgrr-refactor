package live.dgrr.domain.waitingroom.dto.response;

import live.dgrr.domain.waitingroom.entity.WaitingMember;
import lombok.Builder;

@Builder
public record WaitingMemberInfoResponseDto(
        int roomId,
        WaitingMember waitingMember
) {
    public static WaitingMemberInfoResponseDto of(int roomId, WaitingMember waitingMember) {
        return WaitingMemberInfoResponseDto.builder()
                .roomId(roomId)
                .waitingMember(waitingMember)
                .build();
    }
}
