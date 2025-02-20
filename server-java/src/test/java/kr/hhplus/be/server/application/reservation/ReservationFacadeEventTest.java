package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.event.reservation.ReservationEventPublisher;
import kr.hhplus.be.server.domain.event.reservation.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationFacadeEventTest {
    @Mock
    SeatService seatService;

    @Mock
    UserService userService;

    @Mock
    ReservationEventPublisher reservationEventPublisher;

    @Mock
    ConcertScheduleService concertScheduleService;

    @Mock
    ReservationService reservationService;

    @InjectMocks
    private ReservationFacade reservationFacade; // tested class

    User user;
    List<Seat> reservableSeats;
    List<Long> seatIds;

    @BeforeEach
    void setUp() {
        // Mock user
        user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        // Mock reservable seats
        reservableSeats = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Seat seat = mock(Seat.class);
            when(seat.getId()).thenReturn((long) i);
            reservableSeats.add(seat);
        }

        // List of seat IDs to be reserved
        seatIds = new ArrayList<>();
        for (Seat seat : reservableSeats) {
            seatIds.add(seat.getId());
        }

        List<Seat> leftSeat = new ArrayList<>();
        leftSeat.add(Mockito.mock(Seat.class));

        // Mock behavior of services
        when(seatService.findReservableForUpdate(seatIds)).thenReturn(reservableSeats);
        when(userService.findUser(1L)).thenReturn(user);
        when(seatService.findReservable(1L)).thenReturn(leftSeat);
    }


    @DisplayName("예약 생성 성공 시, 예약 플랫폼으로 이벤트가 발생하는 로직이 1회 발생한다.")
    @Test
    void ReservationFacadeEventTest_00() {
        // given
        Long scheduleId = 1L; // Mock schedule ID
        ReservationFacadeDto.ReservationParam param = new ReservationFacadeDto.ReservationParam(scheduleId, seatIds, user.getId());

        // when
        ReservationFacadeDto.ReservationResult result = reservationFacade.reservation(param);

        // then
        assertNotNull(result);
        assertEquals(seatIds.size(), result.reservationInfoList().size());

        // Verify interactions with mock services
        for (Seat seat : reservableSeats) {
            verify(seat, times(1)).reserve();
        }

        // Verify the event publisher is triggered once with the correct event
        verify(reservationEventPublisher, times(1)).success(any(ReservationSuccessEvent.class));
    }
}
