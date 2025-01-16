package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PaymentTest {

    @DisplayName("결제 생성 시, 예약 정보가 없을 경우 ErrorCode : REQUIRE_FIELD_MISSING 에러가 발생한다.")
    @Test
    void create_Test00(){
        // given
        long amount = 100_000L;
        Reservation reservation = mock(Reservation.class);

        // when
        BusinessException exception = assertThrows(BusinessException.class,()-> Payment.create(amount,null));

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());
    }

    @DisplayName("결제 생성 시, 유효하지 않은 AMOUNT 를 할당할 경우 ErrorCode : NOT_VALID_PAYMENT_AMOUNT 에러가 발생한다.")
    @Test
    void create_Test01(){
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

    @DisplayName("결제를 정상적으로 생성하면, status : SUCCESS 가 된다.")
    @Test
    void create_Test02(){
        // given
        long amount = 100_000L;
        Reservation reservation = mock(Reservation.class);

        // when
        Payment newPayment = Payment.create(amount,reservation);

        // then
        assertEquals(PaymentStatus.SUCCESS,newPayment.getPaymentStatus());
    }

    @DisplayName("결제를 정상적으로 생성하고, 결제를 취소하면 status : CANCEL 이 된다.")
    @Test
    void cancel_Test00(){
        // given
        long amount = 100_000L;
        Reservation reservation = mock(Reservation.class);

        // when
        Payment payment = Payment.create(amount,reservation);
        payment.cancel();

        // then
        assertEquals(PaymentStatus.CANCEL,payment.getPaymentStatus());
    }
}