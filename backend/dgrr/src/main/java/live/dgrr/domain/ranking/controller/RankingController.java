package live.dgrr.domain.ranking.controller;

import live.dgrr.domain.ranking.dto.response.RankingMemberResponse;
import live.dgrr.domain.ranking.entity.Season;
import live.dgrr.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class RankingController {

    private final RankingService rankingService;

    // member id 기준 ranking 조회
    @GetMapping("/member-id/{season}")
    public ResponseEntity<RankingMemberResponse> getRankingByMemberId(Authentication authentication, @PathVariable(value="season") Season season) {
        String memberId = authentication.getName();
        RankingMemberResponse response = rankingService.getRankingMember(Long.valueOf(memberId), season);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}