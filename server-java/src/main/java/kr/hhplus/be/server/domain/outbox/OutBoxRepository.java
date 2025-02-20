package kr.hhplus.be.server.domain.outbox;


import java.util.Optional;

public interface OutBoxRepository {
    OutBox create(OutBox outBox);

    OutBox update(OutBox outBox);

    Optional<OutBox> findById(Long outboxId);
}
