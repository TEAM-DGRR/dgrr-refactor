package live.dgrr.domain.member.dto.response;

import live.dgrr.domain.gamehistory.dto.response.GameHistoryWithOpponentInfoResponse;
import live.dgrr.domain.ranking.dto.response.MemberRankingInfoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoResponse(
        MemberResponse member,
        MemberRankingInfoResponse ranking,
        List<GameHistoryWithOpponentInfoResponse> gameHistoryList

) {
    public static MemberInfoResponse of(MemberResponse member,
                                        MemberRankingInfoResponse ranking,
                                        List<GameHistoryWithOpponentInfoResponse> gameHistoryList) {
        return MemberInfoResponse.builder()
                .member(member)
                .ranking(ranking)
                .gameHistoryList(gameHistoryList)
                .build();
    }
}
