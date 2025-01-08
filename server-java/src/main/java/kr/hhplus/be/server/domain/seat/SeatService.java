package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
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

    public List<Seat> findAllReserveAbleWithLock(List<Long> concertScheduleIds){
        List<Seat> seatList = repository.findAllReserveAbleWithLock(concertScheduleIds);
        if (seatList.isEmpty()) {
            throw new BusinessException(ErrorCode.Repository,"예약 가능한 좌석이 존재하지 않습니다.");
        }

        seatList.forEach(item->{
            if(!item.getStatus().equals(SeatStatus.RESERVABLE)){
                throw new BusinessException(ErrorCode.Repository,"예약 불가능한 좌석이 포함되어 있습니다.");
            }
        });

        return  seatList;
    }
}
