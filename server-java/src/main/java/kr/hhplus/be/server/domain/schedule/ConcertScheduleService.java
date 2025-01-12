package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository repository;

    /**
     * 콘서트 스케줄 아이디를 사용하여, 예약 가능한 콘서트 스케줄인지 확인 후 반환
     * @param concertScheduleId
     * @return
     */
    public ConcertSchedule findReservableConcertSchedule(Long concertScheduleId){
        // 1. 존재하는 콘서트 스케줄인지 확인
        ConcertSchedule concertSchedule = findConcertSchedule(concertScheduleId);

        // 2. 예약이 가능한 상태인지 확인
        if(!concertSchedule.isReservable()){
            throw new BusinessException(ErrorCode.SERVICE,"예약이 마감되었습니다.");
        }

        // 3. 예약 가능한 시간대인지 확인
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(concertSchedule.getReservation_start()) || now.isAfter(concertSchedule.getReservation_end())) {
            throw new BusinessException(ErrorCode.SERVICE, "예약 가능한 시간대가 아닙니다.");
        }

        return concertSchedule;
    }

    /**
     * 콘서트 아이디를 사용하여, 예약 가능한 콘서트 일정을 반환
     * findAvailableSchedules -> findReservableSchedules
     * @param concertId
     * @param page
     * @return
     */
    public List<ConcertSchedule> findReservableSchedules(Long concertId, int page){
        return repository.findReservableByConcertIdAndPage(concertId,page);
    }

    /**
     * 콘서트 스케줄 아이디를 사용하여, 콘서트 스케줄을 반환
     * @param scheduleId
     * @return
     */
    public ConcertSchedule findConcertSchedule(Long scheduleId){
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
