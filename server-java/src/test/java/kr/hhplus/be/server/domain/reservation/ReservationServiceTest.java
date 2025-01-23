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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository repository;

    @InjectMocks
    ReservationService reservationService;

    @BeforeEach
    void setUp(){
        Concert concert = Concert.create("김연습","서커스");
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

// reserve
    @DisplayName("예약 시, 예약 아이디 리스트를 넣고 결과가 없을 경우 [ ErrorCode : NOT_FOUND_RESERVATION ] 가 발생한다. ")
    @Test
    void reserve_Test00(){
        // given
        List<Long> reservationIds = new ArrayList<>();
        Long       userId         = 1L;
        when(repository.findByIdsWithLock(reservationIds)).thenReturn(new ArrayList<>());

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

        when(repository.findByIdsWithLock(reservationIds)).thenReturn(reservations);
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

        when(repository.findByIdsWithLock(reservationIds)).thenReturn(reservations);
        doThrow(new BusinessException(ErrorCode.NOT_RESERVATION_OWNER)).when(reservation).isReserveUser(userId);


        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserve(reservationIds,userId));

        // then
        assertEquals(ErrorCode.NOT_RESERVATION_OWNER, exception.getErrorStatus());
    }

// expireAndReturnSeatIdList
    @DisplayName("만료된 예약을 만료시키고 좌석 아이디를 리턴 받는다.")
    @Test
    void expireAndReturnSeatIdList_Test00(){
        // given
        Reservation reservation01 = mock(Reservation.class);
        Reservation reservation02 = mock(Reservation.class);
        Seat seat1 = mock(Seat.class);
        Seat seat2 = mock(Seat.class);
        when(reservation01.getSeat()).thenReturn(seat1);
        when(reservation02.getSeat()).thenReturn(seat2);
        when(seat1.getId()).thenReturn(1L);
        when(seat2.getId()).thenReturn(2L);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation01);
        reservations.add(reservation02);
        when(repository.findExpiredWithLock()).thenReturn(reservations);

        // when
        List<Long> seatIdList = reservationService.expireAndReturnSeatIdList();

        // then
        assertEquals(2,seatIdList.size());
        seatIdList.forEach(item -> {
            if (item != 1L && item != 2L) {
                fail();
            }
        });
    }




}