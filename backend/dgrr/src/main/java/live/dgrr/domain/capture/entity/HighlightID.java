package live.dgrr.domain.capture.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HighlightID implements Serializable {

    @Column(name = "game_room_id")
    private String gameRoomId;

    private int round;

}
