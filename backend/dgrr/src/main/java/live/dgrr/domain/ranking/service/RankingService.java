package live.dgrr.domain.ranking.service;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.domain.ranking.dto.response.RankingMemberResponse;
import live.dgrr.domain.ranking.dto.response.RankingResponse;
import live.dgrr.domain.ranking.entity.Season;
import live.dgrr.domain.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final MemberRepository memberRepository;

    public boolean addRanking(Long memberId, double rating) {
        return rankingRepository.addRanking(memberId, rating);
    }

    public RankingMemberResponse getRankingMember(Long memberId, Season season) {
        Member member = memberRepository.findById(memberId).get();
        List<RankingResponse> rankings = rankingRepository.getRankingMember(member, season);

        return RankingMemberResponse.of(getRanking(memberId, season), rankings);
    }

    public RankingResponse getRanking(Long memberId, Season season) {
        Member member = memberRepository.findById(memberId).get();
        Double rating = rankingRepository.getRatingByMemberIdAndSeason(memberId, season);
        Long rank = rankingRepository.getRankByMemberIdAndSeason(memberId, season);
        return RankingResponse.of(memberId, rating, rank, member.getNickname(), member.getProfileImage());
    }
}
