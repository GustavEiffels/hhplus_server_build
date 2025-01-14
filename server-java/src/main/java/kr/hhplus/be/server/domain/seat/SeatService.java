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
    public List<Seat> findReservable(Long concertScheduleId){
        return repository.findByScheduleId(concertScheduleId);
    }

    public List<Seat> findReservableForUpdate(List<Long> seatIds){
        List<Seat> seatList = repository.findByIdsWithLock(seatIds);
        if (seatList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_SEAT);
        }

        seatList.forEach(item->{
            if(!item.getStatus().equals(SeatStatus.RESERVABLE)){
                throw new BusinessException(ErrorCode.NOT_RESERVABLE_DETECTED);
            }
        });

        return  seatList;
    }

    public List<Long> reservableAndReturnSchedules(List<Long> seatIds){
        return repository.findByIdsWithLock(seatIds).stream()
                .map(item -> {
                    item.reservable();
                    return item.getConcertSchedule().getId();
                })
                .toList();
    }
}
