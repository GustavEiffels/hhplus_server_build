package kr.hhplus.be.server.domain.seat;

import java.util.List;

public interface SeatRepository {


    List<Seat> findByIdsWithLock(List<Long> seatIds);

    List<Seat> findByScheduleId(Long concertScheduleId);
}
