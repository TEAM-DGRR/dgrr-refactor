package live.dgrr.domain.member.repository;

import live.dgrr.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByNickname(String nickname);
}
