package kr.hhplus.be.server.application.event;

import kr.hhplus.be.server.domain.reservation.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationEventListenerTest {

    @InjectMocks
    private ReservationEventListener reservationEventListener;

    private List<Reservation> reservations;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        // Mock 객체 생성
        Reservation reservation1 = Mockito.mock(Reservation.class);
        Reservation reservation2 = Mockito.mock(Reservation.class);

        // getId()가 호출될 때 반환할 값 설정
        when(reservation1.getId()).thenReturn(1L);
        when(reservation2.getId()).thenReturn(2L);

        reservations = List.of(reservation1, reservation2);
    }

    @Test
    @DisplayName("예약 성공 이벤트 핸들러가 정상적으로 메시지를 전송하는지 테스트")
    void reservationSuccessHandlerTest() {
    }
}
