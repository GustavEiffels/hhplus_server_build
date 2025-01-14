package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SeatTest {

    @DisplayName("좌석 번호가 1 ~ 50 사이 자연수가 아니면 [ ErrorCode : INVALID_SEAT_NUMBER ] 가 발생한다.")
    @Test
    void Seat_Test_00(){
        // given
        int seatNo = 51;
        long price  = 10_000L;

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->{
            Seat.builder().seatNo(seatNo).price(price).build();
        });

        // then
        assertEquals(ErrorCode.INVALID_SEAT_NUMBER,businessException.getErrorStatus());
    }

    @DisplayName("좌석 금액은 10,000 ~ 1,000,000 사이 금액이 아니라면, [ ErrorCode : INVALID_SEAT_NUMBER ] 가 발생한다.")
    @Test
    void Seat_Test_01(){
        // given
        int seatNo = 50;
        long price  = 9_000;

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->Seat.builder().seatNo(seatNo).price(price).build());

        // then
        assertEquals(ErrorCode.INVALID_SEAT_PRICE,businessException.getErrorStatus());
    }

    @DisplayName("필수 값인 콘서트 스케줄이 존재하지 않을 경우 , [ ErrorCode : REQUIRE_FIELD_MISSING ] 가 발생한다.")
    @Test
    void Seat_Test_02(){
        // given
        int seatNo = 50;
        long price  = 10_000;

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->Seat.builder().seatNo(seatNo).price(price).build());

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,businessException.getErrorStatus());
    }
}