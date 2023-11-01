package live.dgrr.domain.gamehistory.entity;

import jakarta.persistence.*;
import live.dgrr.domain.game.entity.GameResult;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameHistoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
    private String gameRoomId;
    @Enumerated(EnumType.STRING)
    private GameResult gameResult; //WIN, LOSE, DRAW
    @Enumerated(EnumType.STRING)
    private GameType gameType; //RANDOM, PRIVATE
    private Long gameTime;
    private Integer ratingChange; //해당 게임으로 반영되는 점수
    private Integer holdingTime;
    private String highlightImage;

}
