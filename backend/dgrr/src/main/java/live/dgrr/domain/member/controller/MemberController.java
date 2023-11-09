package live.dgrr.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import live.dgrr.domain.gamehistory.dto.response.GameHistoryWithOpponentInfoResponse;
import live.dgrr.domain.member.dto.request.MemberAddRequest;
import live.dgrr.domain.member.dto.request.MemberRequest;
import live.dgrr.domain.member.dto.response.MemberInfoResponse;
import live.dgrr.domain.member.dto.response.MemberResponse;
import live.dgrr.domain.member.service.MemberService;
import live.dgrr.domain.ranking.service.RankingService;
import live.dgrr.global.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final RankingService rankingService;

    @GetMapping("/member-id")
    public ResponseEntity<MemberInfoResponse> getMyInfo (HttpServletRequest request) {
        log.info("MemberController - getMyInfo");
        String token = request.getHeader("Authorization").replace(JwtProperties.TOKEN_PREFIX, "");
        String memberId = memberService.getIdFromToken(token);
        System.out.println("memberId: " + memberId);
        return new ResponseEntity<>(memberService.getMyInfo(memberId), HttpStatus.OK);
    }

    @GetMapping("/game-history/member-id")
    public ResponseEntity<List<GameHistoryWithOpponentInfoResponse>> getMyGameHistory(HttpServletRequest request) {
        log.info("MemberController - getMyGameHistory");
        String token = request.getHeader("Authorization").replace(JwtProperties.TOKEN_PREFIX, "");
        String memberId = memberService.getIdFromToken(token);
        return new ResponseEntity<>(memberService.getMyGameHistory(memberId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateMember(HttpServletRequest request, @Valid @RequestBody MemberRequest memberRequest) {
        log.info("MemberController - updateMember");
        String token = request.getHeader("Authorization").replace(JwtProperties.TOKEN_PREFIX, "");
        String memberId = memberService.getIdFromToken(token);
        memberService.updateByMember(memberId, memberRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(@RequestBody MemberAddRequest memberAddRequest) {
        MemberResponse memberResponse = memberService.addMember(memberAddRequest);
        rankingService.addRanking(memberResponse.memberId(), 1400);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<Void> nicknameCheck(@PathVariable(value="nickname") String nickname) {
        memberService.checkNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}