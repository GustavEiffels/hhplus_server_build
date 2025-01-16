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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationTest {

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
    }
    Seat seat;


// CREATE
    @DisplayName("예약 생성 시 사용자 정보가 없으면 [ ErrorCode : REQUIRE_FIELD_MISSING ] 가 발생한다.")
    @Test
    void create_Test00(){
        // given
        Seat seat1 = seat;

        // when
        BusinessException exception = assertThrows( BusinessException.class,() -> Reservation.create(null,seat1));

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());
    }


    @DisplayName("예약 생성 시 좌석 정보가 없으면 [ ErrorCode : REQUIRE_FIELD_MISSING ] 가 발생한다.")
    @Test
    void create_Test01(){
        // given
        User user  = User.create("test");

        // when
        BusinessException exception = assertThrows( BusinessException.class,() -> Reservation.create(user,null));

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());

    }

// isReserveUser
    @DisplayName("예약의 주인과 사용자의 정보가 일치하지 않을 경우, [ ErrorCode : NOT_RESERVATION_OWNER ] 가 발생한다.")
    @Test
    void isReserveUser_Test00(){
        // given
        User user   = mock(User.class);
        User user1  = mock(User.class);
        Seat seat1  = seat;
        Reservation reservation = Reservation.create(user,seat1);

        when(user.getId()).thenReturn(1L);
        when(user1.getId()).thenReturn(2L);

        // when
        BusinessException exception = assertThrows( BusinessException.class,() -> reservation.isReserveUser(user1.getId()));

        // then
        assertEquals(ErrorCode.NOT_RESERVATION_OWNER,exception.getErrorStatus());
    }

// isExpired
    @DisplayName("해당 예약이 만료 되었을 경우, [ ErrorCode : EXPIRE_RESERVATION ] 가 발생한다.")
    @Test
    void isExpired_Test00(){
        //given
        User user  = User.create("test");
        Seat seat1 = seat;
        Reservation reservation = Reservation.create(user,seat1);
        reservation.expired();

        // when
        BusinessException exception = assertThrows( BusinessException.class,() -> reservation.isExpired() );

        // then
        assertEquals(ErrorCode.EXPIRE_RESERVATION,exception.getErrorStatus());
    }

// expire
    @DisplayName("해당 예약을 만료 시킬 경우, Status 값이 EXPIRED 가 된다.")
    @Test
    void expired_Test00(){
        //given
        User user  = User.create("test");
        Seat seat1 = seat;
        Reservation reservation = Reservation.create(user,seat1);

        // when
        reservation.expired();

        // then
        assertEquals(ReservationStatus.EXPIRED,reservation.getStatus());
    }

// reserve
    @DisplayName("해당 예약을 예약 처리 시킬 경우, Status 값이 RESERVED 가 된다.")
    @Test
    void reserve_Test00(){
        //given
        User user  = User.create("test");
        Seat seat1 = seat;
        Reservation reservation = Reservation.create(user,seat1);

        // when
        reservation.reserve();

        // then
        assertEquals(ReservationStatus.RESERVED,reservation.getStatus());
    }


}