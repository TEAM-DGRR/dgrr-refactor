package live.dgrr.domain.capture.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.net.URL;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "highlight")
public class Highlight implements Serializable {

    @EmbeddedId
    HighlightID highlightID;

    @Column(name = "capture_url")
    URL captureUrl;
}
