package kr.hhplus.be.server.domain.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository repository;


    /**
     * 콘서트 스케줄 아이디를 사용하여 예약 가능한 좌석들 조회
     * @param concertScheduleId
     * @return
     */
    public List<Seat> findReserveAble(Long concertScheduleId){
        return repository.findAllReserveAble(concertScheduleId);
    }
}
