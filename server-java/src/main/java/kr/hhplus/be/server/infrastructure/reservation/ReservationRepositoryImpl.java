package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository jpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return jpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> save(List<Reservation> reservationList) {
        return jpaRepository.saveAll(reservationList);
    }

    @Override
    public List<Reservation> findExpiredWithLock() {
        return jpaRepository.findExpiredWithLock();
    }

    @Override
    public List<Reservation> findByIdsWithLock(List<Long> reservationsIds) {
        return jpaRepository.findByIds(reservationsIds);
    }
}
