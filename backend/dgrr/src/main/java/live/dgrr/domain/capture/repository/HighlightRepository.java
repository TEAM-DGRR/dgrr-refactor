package live.dgrr.domain.capture.repository;

import live.dgrr.domain.capture.entity.Highlight;
import live.dgrr.domain.capture.entity.HighlightID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<Highlight, HighlightID> {

}
