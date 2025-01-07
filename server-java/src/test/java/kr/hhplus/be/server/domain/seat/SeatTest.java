package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void 좌석_번호가_1에서_50_사이의_자연수가_아니면_예외가_발생한다(){
        // given
        int seatNo = 51;
        int price  = 10_000;

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->{
            Seat.builder().seatNo(seatNo).price(price).build();
        });

        // then
        assertEquals(ErrorCode.Entity,businessException.getErrorStatus());
    }

    @Test
    void 금액은_10_000_에서_100_000_사이_자연수가_아니면_BusinessException이_발생한다(){
        // given
        int seatNo = 50;
        int price  = 9_000;

        // when
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,()->Seat.builder().seatNo(seatNo).price(price).build());

        // then
        assertEquals(ErrorCode.Entity,businessException.getErrorStatus());
    }
}