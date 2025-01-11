package kr.hhplus.be.server.infrastructure.schedule;


import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ConcertScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    @Override
    public List<ConcertSchedule> findReservableScheduleList(Long concertId, int page) {
        return jpaRepository.findAllReserveAbleByConcertId(concertId,page);
    }

    @Override
    public Optional<ConcertSchedule> findById(Long concertScheduleId) {
        return jpaRepository.findById(concertScheduleId);
    }

    @Override
    public Optional<ConcertSchedule> findByIdWithLock(Long scheduleId) {
        return jpaRepository.findByIdWithLock(scheduleId);
    }

    @Override
    public List<ConcertSchedule> findByIdsWithLock(List<Long> scheduleIds) {
        return jpaRepository.findByIdsWithLock(scheduleIds);
    }
}
