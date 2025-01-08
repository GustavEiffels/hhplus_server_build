package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConcertScheduleTest {

    @Test
    void 콘서트_예약_시작_시간_보다_콘서트_예약_종료_시간이_늦으면_BusinessException_예외가_발생(){
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
        assertEquals(ErrorCode.Entity,businessException.getErrorStatus());
        System.out.println(businessException.getMessage());
    }


    @Test
    void 콘서트_예약_종료_시간_보다_콘서트_공연_시작_시간이_늦으면_BusinessException_예외가_발생(){
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
        assertEquals(ErrorCode.Entity,businessException.getErrorStatus());
    }

// -> ConcertSchedule 의 leftTicket 필드가 변경되어 테스트 삭제
//    @Test
//    void 남은_티켓수가_50개_이면_증가_할_수_없습니다(){
//        LocalDateTime startTime = LocalDateTime.now();
//        LocalDateTime endTime   = startTime.plusDays(10);
//        LocalDateTime showTime  = startTime.plusDays(11);
//        Concert concert = Concert.builder().performer("김연습").title("서커스").build();
//
//        ConcertSchedule concertSchedule = ConcertSchedule.builder()
//                .concert(concert)
//                .showTime(showTime)
//                .reserveStartTime(startTime)
//                .reserveEndTime(endTime)
//                .build();
//
//        // when
//        BusinessException exception = Assertions.assertThrows(BusinessException.class, concertSchedule::increaseTicketCnt);
//
//        // then
//        assertEquals(ErrorCode.Entity,exception.getErrorStatus());
//    }


    // -> ConcertSchedule 의 leftTicket 필드가 변경되어 테스트 삭제
//    @Test
//    void 남은_티켓수가_0개_이면_감소_할_수_없습니다(){
//        LocalDateTime startTime = LocalDateTime.now();
//        LocalDateTime endTime   = startTime.plusDays(10);
//        LocalDateTime showTime  = startTime.plusDays(11);
//        Concert concert = Concert.builder().performer("김연습").title("서커스").build();
//
//        ConcertSchedule concertSchedule = ConcertSchedule.builder()
//                .showTime(showTime)
//                .concert(concert)
//                .reserveStartTime(startTime)
//                .reserveEndTime(endTime)
//                .build();
//
//        // when
//        BusinessException exception = Assertions.assertThrows(BusinessException.class, ()->{
//            for(int i = 0; i<51; i++){
//                concertSchedule.decreaseTicketCnt();
//            }
//        });
//
//        // then
//        assertEquals(ErrorCode.Entity,exception.getErrorStatus());
//    }

}