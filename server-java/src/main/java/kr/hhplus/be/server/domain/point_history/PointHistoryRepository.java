package kr.hhplus.be.server.domain.point_history;

import java.util.List;

public interface PointHistoryRepository {

    PointHistory create(PointHistory pointHistory);
    List<PointHistory> create(List<PointHistory> pointHistories);
}
