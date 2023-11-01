package live.dgrr.domain.member.controller;

import live.dgrr.domain.member.dto.response.MemberInfoResponse;
import live.dgrr.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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

}
