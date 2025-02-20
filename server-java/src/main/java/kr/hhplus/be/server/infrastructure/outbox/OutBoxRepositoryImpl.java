package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class OutBoxRepositoryImpl implements OutBoxRepository {
    private final OutBoxJpaRepository jpaRepository;

    @Override
    public OutBox create(OutBox outBox) {
        return jpaRepository.save(outBox);
    }

    @Override
    public OutBox update(OutBox outBox) {
        return jpaRepository.save(outBox);
    }

    @Override
    public Optional<OutBox> findById(Long outboxId) {
        return jpaRepository.findById(outboxId);
    }
}
