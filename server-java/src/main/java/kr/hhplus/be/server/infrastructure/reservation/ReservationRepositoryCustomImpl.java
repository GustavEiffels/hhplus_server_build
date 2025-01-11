package kr.hhplus.be.server.infrastructure.reservation;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.reservation.QReservation.reservation;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory dsl;

    /**
     * 비관적 락 사용
     * fetch join 으로 seat 가져오기
     *
     * @return
     */
    @Override
    public List<Reservation> findExpiredWithLock() {
        return dsl.select(reservation)
                .from(reservation)
                .where(reservation.status.eq(ReservationStatus.Pending)
                        .and(reservation.expiredAt.before(LocalDateTime.now())))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    @Override
    public List<Reservation> findByIdsWithLock(List<Long> reservationIds) {
        return dsl.select(reservation)
                .from(reservation)
                .join(reservation.user)
                .fetchJoin()
                .where(reservation.id.in(reservationIds))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }
}
