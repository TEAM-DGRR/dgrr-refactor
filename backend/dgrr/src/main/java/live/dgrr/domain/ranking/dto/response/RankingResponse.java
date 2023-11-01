package live.dgrr.domain.ranking.dto.response;

import lombok.Builder;

@Builder
public record RankingResponse(Long memberId, Double rating, Long rank) {
    public static RankingResponse of(Long memberId, Double rating, Long rank) {
        return RankingResponse.builder()
                .memberId(memberId)
                .rating(rating)
                .rank(rank)
                .build();
    }
}
