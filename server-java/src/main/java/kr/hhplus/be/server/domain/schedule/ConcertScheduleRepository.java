package kr.hhplus.be.server.domain.schedule;

import java.util.List;

public interface ConcertScheduleRepository {

    List<ConcertSchedule> findReservableScheduleList(Long concertId, int page);
}
