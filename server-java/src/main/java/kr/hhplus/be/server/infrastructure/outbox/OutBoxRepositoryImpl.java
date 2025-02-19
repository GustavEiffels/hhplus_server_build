package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class OutBoxRepositoryImpl implements OutBoxRepository {
    private final OutBoxJpaRepository jpaRepository;
    @Override
    public List<OutBox> create(List<OutBox> outBoxList) {
        return jpaRepository.saveAll(outBoxList);
    }

    @Override
    public OutBox create(OutBox outBox) {
        return jpaRepository.save(outBox);
    }
}
