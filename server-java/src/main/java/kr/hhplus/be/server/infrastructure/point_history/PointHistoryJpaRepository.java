package kr.hhplus.be.server.infrastructure.point_history;

import kr.hhplus.be.server.domain.point_history.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory,Long> {
}
