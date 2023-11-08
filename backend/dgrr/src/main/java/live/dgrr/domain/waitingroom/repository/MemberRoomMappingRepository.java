package live.dgrr.domain.waitingroom.repository;

import live.dgrr.domain.waitingroom.entity.MemberRoomMapping;
import org.springframework.data.repository.CrudRepository;

public interface MemberRoomMappingRepository extends CrudRepository<MemberRoomMapping, Long> {
}
