package kr.hhplus.be.server.domain.point_history;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PointHistoryTest {

    @DisplayName("[사용] 결제 내역 생성 시, payment 가 존재하지 않으면 ErrorCode : REQUIRE_FIELD_MISSING 가 발생한다.")
    @Test
    void createUse_Test00(){
        // given
        Long amount = 100_000L;
        User user = User.create("김연습");

        // when
        BusinessException exception = assertThrows( BusinessException.class,()->PointHistory.createUse(amount,user,null) );

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());
    }

    @DisplayName("[사용] 결제 내역 생성 시, 적절하지 않은 amount 가 할당되면 ErrorCode : INVALID_AMOUNT 가 발생한다.")
    @Test
    void createUse_Test01(){
        // given
        Long amount = -1L;
        User user = User.create("test");
        Payment payment = mock(Payment.class);

        // when
        BusinessException exception = assertThrows( BusinessException.class,()->PointHistory.createUse(amount,user,payment) );

        // then
        assertEquals(ErrorCode.INVALID_AMOUNT,exception.getErrorStatus());
    }

    @DisplayName("[사용] 결제 내역을 성공적으로 생성하면 status : USE 로 설정이 된다.")
    @Test
    void createUse_Test02(){
        // given
        Long amount = 100_000L;
        User user = User.create("test");
        Payment payment = mock(Payment.class);

        // when
        PointHistory pointHistory = PointHistory.createUse(amount,user,payment);


        // then
        assertEquals(amount,pointHistory.getAmount());
        assertEquals(PointHistoryStatus.USE,pointHistory.getStatus());
    }

}