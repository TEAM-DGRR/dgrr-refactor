package live.dgrr.domain.member.entity;

import jakarta.persistence.*;
import live.dgrr.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String kakaoId;
    private String nickname;
    private String profileImage;
    private String description;
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public void updateMember(String nickname, String profileImage, String description) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.description = description;
    }
}
