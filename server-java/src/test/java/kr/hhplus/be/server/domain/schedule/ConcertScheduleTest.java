package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConcertScheduleTest {


    @DisplayName("콘서트(콘서트 스케줄)의 [ 예약 시작 시간 ]이 [ 예약 시작 시간 ] 보다 늦으면 ErrorCode : RESCHEDULE_ERROR_INVALID_SHOWTIME")
    @Test
    void concertSchedule_reschedule_test00(){
        // given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime   = startTime.minusHours(1);
        LocalDateTime showTime  = startTime.plusDays(3);
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();

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
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();

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
        Concert concert = Concert.builder().performer("김연습").title("서커스").build();

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


}