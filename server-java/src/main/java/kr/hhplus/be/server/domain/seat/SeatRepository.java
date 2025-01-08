package kr.hhplus.be.server.domain.seat;

import java.util.List;

public interface SeatRepository {

    List<Seat> findAllReserveAble(Long concertScheduleId);
}
