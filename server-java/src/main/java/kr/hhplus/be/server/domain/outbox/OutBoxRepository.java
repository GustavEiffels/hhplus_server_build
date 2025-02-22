package kr.hhplus.be.server.domain.outbox;

import java.util.List;

public interface OutBoxRepository {
    List<OutBox> create(List<OutBox> outBoxList);
}
