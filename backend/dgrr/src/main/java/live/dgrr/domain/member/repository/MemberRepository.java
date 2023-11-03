package live.dgrr.domain.member.repository;

import live.dgrr.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByNickname(String nickname);

    Optional<Member> findByKakaoId(String kakaoId);
}
