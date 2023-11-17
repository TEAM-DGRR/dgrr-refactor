package live.dgrr.domain.ranking.dto.request;

import lombok.Builder;

@Builder
public record RankingRequest(Long memberId, double rating) {

}
