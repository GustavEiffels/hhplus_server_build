package kr.hhplus.be.server.domain.outbox;


public interface OutBoxRepository {
    OutBox create(OutBox outBox);
}
