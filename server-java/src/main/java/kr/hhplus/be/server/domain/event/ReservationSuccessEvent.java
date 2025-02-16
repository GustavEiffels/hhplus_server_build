package kr.hhplus.be.server.domain.event;

import kr.hhplus.be.server.domain.reservation.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationSuccessEvent {
    List<Reservation> reservations;
    Long userId;
}
