package kr.hhplus.be.server.domain.schedule;

import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository {

    List<ConcertSchedule> findReservableScheduleList(Long concertId, int page);


    Optional<ConcertSchedule> findByConcertScheduleId(Long concertScheduleId);
}
