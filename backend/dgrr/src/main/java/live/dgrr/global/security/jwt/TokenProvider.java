package live.dgrr.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import live.dgrr.global.config.jwt.JwtConfig;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final Key key;

    public TokenProvider(JwtConfig jwtConfig) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public TokenDto generateTokenDto(String kakaoId, Long memberId) {

        long now = (new Date()).getTime();

        Date tokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(kakaoId)
                .claim("id", memberId)
                .setExpiration(tokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return TokenDto.of(BEARER_TYPE, accessToken, tokenExpiresIn.getTime());
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("id") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("id").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new GeneralException(ErrorCode.WRONG_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new GeneralException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorCode.WRONG_JWT_TOKEN);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}