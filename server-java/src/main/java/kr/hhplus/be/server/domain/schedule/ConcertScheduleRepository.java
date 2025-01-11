package kr.hhplus.be.server.domain.schedule;

import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository {

    List<ConcertSchedule> findReservableScheduleList(Long concertId, int page);


    Optional<ConcertSchedule> findById (Long concertScheduleId);

    Optional<ConcertSchedule> findByIdWithLock(Long scheduleId);

    List<ConcertSchedule> findByIdsWithLock(List<Long> scheduleIds);
}
