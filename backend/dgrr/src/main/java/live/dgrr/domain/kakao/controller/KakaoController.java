package live.dgrr.domain.kakao.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import live.dgrr.domain.kakao.service.KakaoService;
import live.dgrr.domain.member.dto.request.MemberAddRequest;
import live.dgrr.domain.member.dto.response.MemberResponse;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/kakao")
public class KakaoController {
    private final KakaoService kakaoService;
    private final RankingService rankingService;
    private static final int START_SCORE = 1400;

    // 카카오 로그인
    @GetMapping("/kakaoCallback")
    @CrossOrigin
    public ResponseEntity<?> kakaoLogin(@RequestParam(value="code")String code, HttpSession session, HttpServletResponse response) {
        String responses;
        String token = kakaoService.getKakaoAccessToken(code);

        String id = kakaoService.createKakaoUser(token);
        MemberResponse member = kakaoService.getMemberByKakaoId(id);

        if(member == null) {
            HashMap<String, String> map = new HashMap<>();
            responses = "signUp";
            map.put("key", responses);
            map.put("id", id);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }else{
            session.setAttribute("loginUser", member);
            String sessionId = session.getId();
            Cookie cookie = new Cookie("JSESSIONID", sessionId);
            cookie.setPath("/");
            response.addCookie(cookie);
            return new ResponseEntity<>(member, HttpStatus.OK);
        }
    }

}