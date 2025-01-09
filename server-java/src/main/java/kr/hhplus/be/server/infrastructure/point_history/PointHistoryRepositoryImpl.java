package kr.hhplus.be.server.infrastructure.point_history;

import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.point_history.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryJpaRepository jpaRepository;


    @Override
    public PointHistory create(PointHistory pointHistory) {
        return  jpaRepository.save(pointHistory);
    }

    @Override
    public List<PointHistory> create(List<PointHistory> pointHistories) {
        return jpaRepository.saveAll(pointHistories);
    }
}
