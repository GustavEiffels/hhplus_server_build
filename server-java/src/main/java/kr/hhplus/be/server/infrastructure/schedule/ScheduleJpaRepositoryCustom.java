package kr.hhplus.be.server.infrastructure.schedule;

import kr.hhplus.be.server.domain.schedule.ConcertSchedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleJpaRepositoryCustom {

    List<ConcertSchedule> findAllReserveAbleByConcertId(Long concertId, int page);

    Optional<ConcertSchedule> findByIdWithLock(Long scheduleId);

    List<ConcertSchedule> findByIdsWithLock(List<Long> scheduleIds);
}
