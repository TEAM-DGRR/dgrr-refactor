package live.dgrr.domain.ranking.dto.response;

import lombok.Builder;

@Builder
public record RankingResponse(Long memberId, Double rating, Long rank, String nickname, String profileImage) {
    public static RankingResponse of(Long memberId, Double rating, Long rank, String nickname, String profileImage) {
        return RankingResponse.builder()
                .memberId(memberId)
                .rating(rating)
                .rank(rank)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
