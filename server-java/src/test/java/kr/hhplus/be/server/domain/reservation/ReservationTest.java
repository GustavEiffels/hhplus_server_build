package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void 예약_생성시_사용자_정보가_존재하지_않으면_BaseException_예외가_발생(){
        User user = User.builder().name("김바부").build();
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();
        ConcertSchedule schedule = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(LocalDateTime.now())
                .reserveEndTime(LocalDateTime.now().plusHours(3))
                .showTime(LocalDateTime.now().plusDays(3))
                .build();
        Seat seat = Seat.builder()
                .seatNo(10)
                .price(100_000)
                .concertSchedule(schedule)
                .build();

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    Reservation re = Reservation.builder()
                            .seat(seat)
                            .build();
                });
        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }


    @Test
    void 예약_생성시_좌석_정보가_존재하지_않으면_BaseException_예외가_발생(){
        User user = User.builder().name("김바부").build();
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();
        ConcertSchedule schedule = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(LocalDateTime.now())
                .reserveEndTime(LocalDateTime.now().plusHours(3))
                .showTime(LocalDateTime.now().plusDays(3))
                .build();
        Seat seat = Seat.builder()
                .seatNo(10)
                .price(100_000)
                .concertSchedule(schedule)
                .build();

        // when
        BusinessException be = assertThrows(
                BusinessException.class,() -> {
                    Reservation re = Reservation.builder()
                            .user(user)
                            .build();
                });
        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }
}