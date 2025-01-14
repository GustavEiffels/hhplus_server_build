package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PaymentTest {
    @Test
    void 예약이_없을경우_BusinessException_이_발생한다(){
        // given
        long amount = 100_000L;
        Reservation reservation = mock(Reservation.class);

        // when
        BusinessException exception = assertThrows(BusinessException.class,()-> Payment.create(amount,null));

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());
    }

    @Test
    void 결제_금액이_0보다_작을_경우_BusinessException_ErrorCode_Entity_가_발생한다(){
        // given
        long amount = -1L;
        Reservation reservation = mock(Reservation.class);


        // when
        BusinessException exception = assertThrows(BusinessException.class,()->{
            Payment.create(amount,reservation);
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_PAYMENT_AMOUNT,exception.getErrorStatus());
    }


}