package live.dgrr.global.security.jwt;

import lombok.Builder;

@Builder
public record TokenDto(
        String grantType,
        String accessToken,
        Long tokenExpiresIn
) {
    public static TokenDto of(String grantType, String accessToken, Long tokenExpiresIn) {
        return TokenDto.builder()
                .grantType(grantType)
                .accessToken(accessToken)
                .tokenExpiresIn(tokenExpiresIn)
                .build();
    }
}