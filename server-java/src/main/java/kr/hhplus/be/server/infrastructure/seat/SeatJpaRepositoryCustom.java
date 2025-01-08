package kr.hhplus.be.server.infrastructure.seat;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public interface SeatJpaRepositoryCustom {

    List<Seat> findAllReserveAble(Long concertId);

    List<Seat> findAllReserveAbleWithLock(List<Long> concertScheduleIds);
}
