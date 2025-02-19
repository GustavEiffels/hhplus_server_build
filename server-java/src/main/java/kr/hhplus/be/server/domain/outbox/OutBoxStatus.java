package kr.hhplus.be.server.domain.outbox;

public enum OutBoxStatus {
    PENDING,    // 아직 처리되지 않은 상태
    PROCESSED,  // 이벤트가 성공적으로 처리된 상태
    FAILED      // 이벤트 처리 실패 상태
}
