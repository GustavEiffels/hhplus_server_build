package kr.hhplus.be.server.domain.point_history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService{

    private final PointHistoryRepository pointHistoryRepository;


    /**
     * Point History 생성
     * @param pointHistory
     * @return
     */
    public PointHistory create(PointHistory pointHistory){
        return pointHistoryRepository.create(pointHistory);
    }

    public List<PointHistory> create(List<PointHistory> pointHistories){
        return pointHistoryRepository.create(pointHistories);
    }
}
