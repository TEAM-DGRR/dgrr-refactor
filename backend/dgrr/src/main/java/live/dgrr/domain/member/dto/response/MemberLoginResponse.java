package live.dgrr.domain.member.dto.response;

public record MemberLoginResponse(
        String token,
        MemberResponse memberResponse
) {
}
