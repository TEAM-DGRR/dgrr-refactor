package live.dgrr.domain.game.repository;

import live.dgrr.domain.game.entity.GameRoom;
import org.springframework.data.repository.CrudRepository;

public interface GameRoomRepository extends CrudRepository<GameRoom, String> {
}
