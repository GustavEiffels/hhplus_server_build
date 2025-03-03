package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepositoryCustom{
    List<Reservation> findExpiredWithLock();


    List<Reservation> findByIdsWithLock(List<Long> reservationIds);

    List<Reservation> findByIds(List<Long> reservationIds);

}
