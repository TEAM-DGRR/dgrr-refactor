package live.dgrr.domain.ranking.controller;

import live.dgrr.domain.ranking.dto.response.RankingMemberResponse;
import live.dgrr.domain.ranking.entity.Season;
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
    @GetMapping("/member-id/{memberId}/{season}") // TODO: 로그인 구현 후 token 값 가져오는 걸로 바꾸기
    public ResponseEntity<RankingMemberResponse> getRankingByMemberId(@PathVariable(value="memberId") Long memberId, @PathVariable(value="season") Season season) {
        RankingMemberResponse response = rankingService.getRankingMember(memberId, season);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
