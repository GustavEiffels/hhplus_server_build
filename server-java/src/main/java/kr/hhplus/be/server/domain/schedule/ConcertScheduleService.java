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

    /**
     * 콘서트 아이디를 사용하여, 예약 가능한 콘서트 일정을 반환
     * @param concertId
     * @param page
     * @return
     */
    public List<ConcertSchedule> findAvailableSchedules(Long concertId, int page){
        return repository.findReservableScheduleList(concertId,page);
    }

    /**
     * 콘서트 스케줄 아이디를 사용하여, 콘서트 스케줄을 반환
     * @param scheduleId
     * @return
     */
    public ConcertSchedule findById(Long scheduleId){
        return repository.findById(scheduleId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"해당 날짜의 공연을 찾지 못 하였습니다."));
    }

    public ConcertSchedule findByIdWithLock(Long scheduleId){
        return repository.findByIdWithLock(scheduleId)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"해당 날짜의 공연을 찾지 못 하였습니다."));
    }


    public List<ConcertSchedule> findByIdsWithLock(List<Long> scheduleIds){
        return repository.findByIdsWithLock(scheduleIds);
    }




}
