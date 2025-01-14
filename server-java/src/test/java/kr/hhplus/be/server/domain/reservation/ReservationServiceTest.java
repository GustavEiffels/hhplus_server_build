package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository repository;

    @InjectMocks
    ReservationService reservationService;

    @BeforeEach
    void setUp(){
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();
        ConcertSchedule schedule = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(LocalDateTime.now())
                .reserveEndTime(LocalDateTime.now().plusHours(3))
                .showTime(LocalDateTime.now().plusDays(3))
                .build();
        seat = Seat.builder()
                .seatNo(10)
                .price(100_000L)
                .concertSchedule(schedule)
                .build();

        user = User.create("test");

    }
    Seat seat;
    User user;


    @DisplayName("예약 시, 예약 아이디 리스트를 넣고 결과가 없을 경우 [ ErrorCode : NOT_FOUND_RESERVATION ] 가 발생한다. ")
    @Test
    void reserve_Test00(){
        // given
        List<Long> reservationIds = new ArrayList<>();
        Long       userId         = 1L;
        when(repository.fetchFindByIdsWithLock(reservationIds)).thenReturn(new ArrayList<>());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserve(reservationIds,userId));

        // then
        assertEquals(ErrorCode.NOT_FOUND_RESERVATION, exception.getErrorStatus());
    }

    @DisplayName("반환 한 예약 들 중, 만료된 예약이 존재하는 경우 [ ErrorCode : EXPIRE_RESERVATION ] 가 발생한다.")
    @Test
    void reserve_Test01(){
        // given
        List<Long> reservationIds = new ArrayList<>();
        Long       userId         = 1L;
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation = mock(Reservation.class);
        reservations.add(reservation);

        when(repository.fetchFindByIdsWithLock(reservationIds)).thenReturn(reservations);
        doThrow(new BusinessException(ErrorCode.EXPIRE_RESERVATION)).when(reservation).isExpired();


        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserve(reservationIds,userId));

        // then
        assertEquals(ErrorCode.EXPIRE_RESERVATION, exception.getErrorStatus());
    }


    @DisplayName("반환 한 예약 들 중, 예약자와 사용자가 일치하지 않는 경우 [ ErrorCode : NOT_RESERVATION_OWNER ] 가 발생한다.")
    @Test
    void reserve_Test02(){
        // given
        List<Long> reservationIds = new ArrayList<>();
        Long       userId         = 1L;
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation = mock(Reservation.class);
        reservations.add(reservation);

        when(repository.fetchFindByIdsWithLock(reservationIds)).thenReturn(reservations);
        doThrow(new BusinessException(ErrorCode.NOT_RESERVATION_OWNER)).when(reservation).isReserveUser(userId);


        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserve(reservationIds,userId));

        // then
        assertEquals(ErrorCode.NOT_RESERVATION_OWNER, exception.getErrorStatus());
    }


}