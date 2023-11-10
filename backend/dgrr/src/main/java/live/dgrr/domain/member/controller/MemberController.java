package live.dgrr.domain.member.controller;

import jakarta.validation.Valid;
import live.dgrr.domain.gamehistory.dto.response.GameHistoryWithOpponentInfoResponse;
import live.dgrr.domain.member.dto.request.MemberAddRequest;
import live.dgrr.domain.member.dto.request.MemberRequest;
import live.dgrr.domain.member.dto.response.MemberInfoResponse;
import live.dgrr.domain.member.dto.response.MemberResponse;
import live.dgrr.domain.member.service.MemberService;
import live.dgrr.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<MemberInfoResponse> getMyInfo (Authentication authentication) {
        log.info("MemberController - getMyInfo");
        String memberId = authentication.getName();
        return new ResponseEntity<>(memberService.getMyInfo(memberId), HttpStatus.OK);
    }

    @GetMapping("/game-history/member-id")
    public ResponseEntity<List<GameHistoryWithOpponentInfoResponse>> getMyGameHistory(Authentication authentication) {
        log.info("MemberController - getMyGameHistory");
        String memberId = authentication.getName();
        return new ResponseEntity<>(memberService.getMyGameHistory(memberId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateMember(Authentication authentication, @Valid @RequestBody MemberRequest memberRequest) {
        log.info("MemberController - updateMember");
        String memberId = authentication.getName();
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