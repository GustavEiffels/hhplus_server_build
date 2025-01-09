package kr.hhplus.be.server.domain.point_history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
