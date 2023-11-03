package live.dgrr.domain.member.controller;

import jakarta.validation.Valid;
import live.dgrr.domain.gamehistory.dto.response.GameHistoryWithOpponentInfoResponse;
import live.dgrr.domain.member.dto.request.MemberAddRequest;
import live.dgrr.domain.member.dto.request.MemberRequest;
import live.dgrr.domain.member.dto.response.MemberInfoResponse;
import live.dgrr.domain.member.dto.response.MemberResponse;
import live.dgrr.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member-id")
    public ResponseEntity<MemberInfoResponse> getMyInfo (Principal principal) {
        log.info("MemberController - getMyInfo");
        //TODO: memberId 수정 필요
        String memberId = "1";
        return new ResponseEntity<>(memberService.getMyInfo(memberId), HttpStatus.OK);
    }

    @GetMapping("/game-history/member-id")
    public ResponseEntity<List<GameHistoryWithOpponentInfoResponse>> getMyGameHistory(Principal principal) {
        log.info("MemberController - getMyGameHistory");
        //TODO: memberId 수정 필요
        String memberId = "1";
        return new ResponseEntity<>(memberService.getMyGameHistory(memberId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateMember(Principal principal, @Valid @RequestBody MemberRequest memberRequest) {
        log.info("MemberController - updateMember");
        //TODO: memberId 수정 필요
        String memberId = "1";
        memberService.updateByMember(memberId, memberRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(@RequestBody MemberAddRequest memberAddRequest) {
        MemberResponse memberResponse = memberService.addMember(memberAddRequest);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

}
