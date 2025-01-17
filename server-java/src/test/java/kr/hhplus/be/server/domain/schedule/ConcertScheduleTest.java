package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ConcertScheduleTest {


    @DisplayName("콘서트(콘서트 스케줄)의 [ 예약 시작 시간 ]이 [ 예약 시작 시간 ] 보다 늦으면 ErrorCode : RESCHEDULE_ERROR_INVALID_SHOWTIME")
    @Test
    void concertSchedule_reschedule_test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.minusHours(1);
        LocalDateTime showTime  = startTime.plusDays(3);
        Concert concert = Concert.create("김연습","서커스");

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->{
           ConcertSchedule schedule = ConcertSchedule.builder()
                   .concert(concert)
                   .reserveStartTime(startTime)
                   .reserveEndTime(endTime)
                   .showTime(showTime)
                   .build();
        });

        // then
        assertEquals(ErrorCode.RESCHEDULE_ERROR_END_BEFORE_START,businessException.getErrorStatus());
    }


    @DisplayName("콘서트(콘서트 스케줄)의 [ 예약 마감 시간 ]이 [ 공연 시작 시간 ]보다 늦으면 ErrorCode : RESCHEDULE_ERROR_INVALID_SHOWTIME")
    @Test
    void concertSchedule_reschedule_test01(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.plusDays(10);
        LocalDateTime showTime  = startTime.plusDays(8);
        Concert concert = Concert.create("김연습","서커스");

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->{
            ConcertSchedule schedule = ConcertSchedule.builder()
                    .reserveStartTime(startTime)
                    .reserveEndTime(endTime)
                    .showTime(showTime)
                    .concert(concert)
                    .build();
        });

        // then
        assertEquals(ErrorCode.RESCHEDULE_ERROR_INVALID_SHOWTIME,businessException.getErrorStatus());
    }

    @DisplayName("필수 값인 endTime 을 설정하지 않으면, ErrorCode : REQUIRE_FIELD_MISSING 가 발생한다.")
    @Test
    void concertSchedule_constructor_test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.plusDays(2);
        LocalDateTime showTime  = startTime.plusDays(8);
        Concert concert = Concert.create("김연습","서커스");

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->{
            ConcertSchedule schedule = ConcertSchedule.builder()
                    .reserveStartTime(startTime)
                    .showTime(showTime)
                    .concert(concert)
                    .build();
        });

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,businessException.getErrorStatus());
    }

    @DisplayName("콘서트 스케줄 생성 시, isReservable 를 호출하면 true 를 반환 받는다. ")
    @Test
    void isReservable_Test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.plusDays(2);
        LocalDateTime showTime  = startTime.plusDays(8);
        Concert concert = Concert.create("김연습","서커스");
        ConcertSchedule schedule = ConcertSchedule.builder()
                .reserveStartTime(startTime)
                .reserveEndTime(endTime)
                .showTime(showTime)
                .concert(concert)
                .build();

        // when & then
        assertTrue(schedule.isReservable());
    }

    @DisplayName("콘서트 스케줄을 생성하고 enableReservation 을 호출하고 isReserve() 를 호출시 true 를 반환 받는다.")
    @Test
    void enableReservation_Test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.plusDays(2);
        LocalDateTime showTime  = startTime.plusDays(8);
        Concert concert = Concert.create("김연습","서커스");
        ConcertSchedule schedule = ConcertSchedule.builder()
                .reserveStartTime(startTime)
                .reserveEndTime(endTime)
                .showTime(showTime)
                .concert(concert)
                .build();

        // when
        schedule.enableReservation();

        // then
        assertTrue(schedule.isReservable());
    }

    @DisplayName("콘서트 스케줄을 생성하고 disableReservation 을 호출하고 isReserve() 를 호출시 false 를 반환 받는다.")
    @Test
    void disableReservation_Test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.plusDays(2);
        LocalDateTime showTime  = startTime.plusDays(8);
        Concert concert = Concert.create("김연습","서커스");
        ConcertSchedule schedule = ConcertSchedule.builder()
                .reserveStartTime(startTime)
                .reserveEndTime(endTime)
                .showTime(showTime)
                .concert(concert)
                .build();

        // when
        schedule.disableReservation();

        // then
        assertFalse(schedule.isReservable());
    }


}