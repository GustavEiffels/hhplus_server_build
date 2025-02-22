package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class OutBoxRepositoryImpl implements OutBoxRepository {
    @Override
    public List<OutBox> create(List<OutBox> outBoxList) {
        return null;
    }
}
