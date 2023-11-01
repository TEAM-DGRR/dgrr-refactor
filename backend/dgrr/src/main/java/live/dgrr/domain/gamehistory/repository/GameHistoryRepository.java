package live.dgrr.domain.gamehistory.repository;

import live.dgrr.domain.gamehistory.entity.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameHistoryRepository extends JpaRepository<GameHistory,Long> {
    List<GameHistory> findTop3ByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT gh " +
            "FROM GameHistory gh " +
            "WHERE gh.gameRoomId = :gameRoomId " +
            "AND gh.member.memberId != :memberId")
    Optional<GameHistory> findByMember_OpponentMemberIdForGameHistory(String gameRoomId, Long memberId);
}
