package kr.hhplus.be.server.infrastructure.seat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
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
    public List<Seat> findByIdsWithLock(List<Long> seatIds) {
        return dsl.selectFrom(seat)
                .where(seat.id.in(seatIds))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    @Override
    public List<Seat> findByScheduleId(Long concertScheduleId) {
        return dsl.selectFrom(seat)
                .innerJoin(concertSchedule)
                .on(seat.concertSchedule.eq(concertSchedule))
                .where(concertSchedule.id.eq(concertScheduleId)
                        .and(seat.status.eq(SeatStatus.RESERVABLE)))
                .fetch();
    }

    @Override
    public List<Seat> findByIds(List<Long> seatIds) {
        return dsl.selectFrom(seat)
                .where(seat.id.in(seatIds))
                .fetch();
    }
}
