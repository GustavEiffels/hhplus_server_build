package kr.hhplus.be.server.domain.seat;

import java.util.List;

public interface SeatRepository {


    List<Seat> findAllByIdsWithLock(List<Long> seatIds);

    List<Seat> findByScheduleId(Long concertScheduleId);
}
