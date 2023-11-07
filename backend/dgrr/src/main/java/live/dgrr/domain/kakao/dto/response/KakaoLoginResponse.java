package live.dgrr.domain.kakao.dto.response;

import live.dgrr.domain.member.dto.response.MemberResponse;
import lombok.Builder;

@Builder
public record KakaoLoginResponse(
        String key,
        String id,
        MemberResponse member
) {
    public static KakaoLoginResponse of(String key, String id, MemberResponse member) {
        return KakaoLoginResponse.builder()
                .key(key)
                .id(id)
                .member(member)
                .build();
    }
}
