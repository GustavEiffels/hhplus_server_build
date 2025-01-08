package kr.hhplus.be.server.infrastructure.seat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.QConcertSchedule;
import kr.hhplus.be.server.domain.seat.QSeat;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.hhplus.be.server.domain.schedule.QConcertSchedule.concertSchedule;
import static kr.hhplus.be.server.domain.seat.QSeat.seat;

@RequiredArgsConstructor
public class SeatJpaRepositoryCustomImpl implements SeatJpaRepositoryCustom {

    private final JPAQueryFactory dsl;

    @Override
    public List<Seat> findAllReserveAble(Long concertId) {
        return dsl.selectFrom(seat)
                .innerJoin(concertSchedule)
                .on(seat.concertSchedule.eq(concertSchedule))
                .where(seat.status.eq(SeatStatus.RESERVABLE))
                .fetch();
    }
}
