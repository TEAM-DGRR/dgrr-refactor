package live.dgrr.domain.ranking.dto;

import lombok.Builder;

@Builder
public record RankingRequest(Long memberId, double score) {

}
