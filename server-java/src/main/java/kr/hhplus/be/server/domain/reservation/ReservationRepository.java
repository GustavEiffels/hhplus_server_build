package kr.hhplus.be.server.domain.reservation;

import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    // 만료된 예약을 찾아서
    List<Reservation> findExpiredWithLock();

    List<Reservation> findByIdsWithLock(List<Long> reservationsIds);
}
