package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutBoxJpaRepository extends JpaRepository<OutBox,Long> {
}
