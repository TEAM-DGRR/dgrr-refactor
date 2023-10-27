package live.dgrr.domain.ranking.controller;

import live.dgrr.domain.ranking.dto.RankingMemberResponse;
import live.dgrr.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
@Slf4j
public class RankingController {

    private final RankingService rankingService;

    // member id 기준 ranking 조회
    @GetMapping("/member-id/{memberId}")
    public ResponseEntity<RankingMemberResponse> getRankingByMemberId(@PathVariable Long memberId) {
        RankingMemberResponse response = rankingService.getRankingMember(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
