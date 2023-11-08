package live.dgrr.domain.kakao.controller;

import live.dgrr.domain.kakao.dto.response.KakaoLoginResponse;
import live.dgrr.domain.kakao.service.KakaoService;
import live.dgrr.domain.member.dto.response.MemberLoginResponse;
import live.dgrr.domain.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "Authorization")
@RequestMapping("/api/v1/kakao")
public class KakaoController {
    private final KakaoService kakaoService;

    // 카카오 로그인
    @GetMapping("/kakaoCallback")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestParam(value="code")String code) {
        String token = kakaoService.getKakaoAccessToken(code);
        KakaoLoginResponse response;
        String id = kakaoService.createKakaoUser(token);
        MemberResponse member = kakaoService.getMemberByKakaoId(id);
        if(member == null) { // 멤버가 없으면 회원가입
            response = KakaoLoginResponse.of("signUp", id, null);
        }else { // 멤버가 있다면 로그인
            response = KakaoLoginResponse.of("login", null, member);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/login/{kakaoId}")
    public ResponseEntity<MemberLoginResponse> login(@PathVariable(value="kakaoId") String kakaoId) {
        MemberLoginResponse response = kakaoService.getTokenFromMember(kakaoId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

}