package live.dgrr.domain.member.service;

import live.dgrr.domain.gamehistory.repository.GameHistoryRepository;
import live.dgrr.domain.member.dto.request.MemberRequest;
import live.dgrr.domain.member.dto.response.MemberInfoResponse;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.entity.MemberRole;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.domain.ranking.repository.RankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    GameHistoryRepository gameHistoryRepository;
    @Mock
    RankingRepository rankingRepository;
    @InjectMocks
    MemberService memberService;

    Long memberOneId;
    Long memberTwoId;
    Double memberOneRating;
    Double memberTwoRating;
    Member memberOne;
    Member memberTwo;

    String nicknameUpdate;


    @BeforeEach
    void setUp() {
        memberOneId = 1L;
        memberTwoId = 2L;
        memberOneRating = 1401.0;
        memberTwoRating = 1402.0;
        nicknameUpdate = "닉네임 수정";
        memberOne = new Member(memberOneId, "kakao1", "Player1", "jpg1", "des1", MemberRole.USER);
        memberTwo = new Member(memberTwoId, "kakao2", "Player2", "jpg2", "des2", MemberRole.USER);
    }

    @Test
    void 멤버_정보_가져오기 () {

        when(memberRepository.findById(memberOneId)).thenReturn(java.util.Optional.of(memberOne));
        when(rankingRepository.getRatingByMemberId(memberOneId)).thenReturn(memberOneRating);
        when(gameHistoryRepository.findTop3ByMember_MemberIdOrderByCreatedAtDesc(anyLong())).thenReturn(new ArrayList<>()); // 빈 리스트나 모의 데이터 반환

        //when
        MemberInfoResponse infoResponse = memberService.getMyInfo(String.valueOf(memberOneId));

        // then
        assertThat(infoResponse).isNotNull();
        assertThat(infoResponse.member().memberId()).isEqualTo(Long.parseLong(String.valueOf(memberOneId)));
        assertThat(infoResponse.ranking().rating()).isEqualTo(memberOneRating);

    }

    @Test
    void 멤버_정보_수정() {

        MemberRequest memberRequest = new MemberRequest(memberOneId, nicknameUpdate, "jpg수정", "des수정");
        when(memberRepository.findById(memberOneId)).thenReturn(java.util.Optional.of(memberOne));

        //when
        memberService.updateByMember(String.valueOf(memberOneId), memberRequest);

        //then
        assertThat(memberOne.getNickname()).isEqualTo(nicknameUpdate);
    }

}
