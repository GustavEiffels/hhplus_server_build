package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository repository;

    // lock 을 거는지
    public List<ConcertSchedule> findReservableScheduleList(Long concertId, int page){
        return repository.findReservableScheduleList(concertId,page);
    }

    public ConcertSchedule find(Long scheduleId){

        return repository.findByConcertScheduleId(scheduleId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"해당 날짜의 공연을 찾지 못 하였습니다."));
    }

}
