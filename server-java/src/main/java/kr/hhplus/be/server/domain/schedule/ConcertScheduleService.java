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
        ConcertSchedule concertSchedule = findSchedule(concertScheduleId);

        // 2. 예약이 가능한 상태인지 확인
        if(!concertSchedule.isReservable()){
            throw new BusinessException(ErrorCode.RESERVATION_END);
        }

        // 3. 예약 가능한 시간대인지 확인
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(concertSchedule.getReservation_start()) || now.isAfter(concertSchedule.getReservation_end())) {
            throw new BusinessException(ErrorCode.NOT_RESERVABLE_TIME);
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
    public ConcertSchedule findSchedule(Long scheduleId){
        return repository.findById(scheduleId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_CONCERT_SCHEDULE));
    }

    /**
     * 콘서트 스케줄 아이디를 사용하여, 콘서트 스케줄 반환
     * - 비관적 락을 사용
     * @param scheduleId
     * @return
     */
    public ConcertSchedule findScheduleForUpdate(Long scheduleId){
        return repository.findByIdWithLock(scheduleId)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_CONCERT_SCHEDULE));
    }


    public void reservable(List<Long> concertScheduleIds){
        repository.findByIdsWithLock(concertScheduleIds).forEach(ConcertSchedule::enableReservation);
    }







}
