package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxJpaRepositoryCustom {
    List<OutBox> findByStatusPending();

    List<OutBox> findDeleteList();
}
