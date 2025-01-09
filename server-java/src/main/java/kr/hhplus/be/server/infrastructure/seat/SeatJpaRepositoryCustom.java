package kr.hhplus.be.server.infrastructure.seat;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public interface SeatJpaRepositoryCustom {

    List<Seat> findAllByIdsWithLock(List<Long> seatIds);

    List<Seat> findByScheduleId(Long concertScheduleId);
}
