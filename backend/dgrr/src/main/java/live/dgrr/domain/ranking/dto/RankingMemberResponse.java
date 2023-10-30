package live.dgrr.domain.ranking.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RankingMemberResponse(RankingResponse memberRank, List<RankingResponse> rankings) {
    public static RankingMemberResponse of(RankingResponse memberRank, List<RankingResponse> rankings) {
        return RankingMemberResponse.builder()
                .memberRank(memberRank)
                .rankings(rankings)
                .build();
    }
}
