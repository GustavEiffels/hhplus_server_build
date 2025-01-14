package kr.hhplus.be.server.domain.point_history;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PointHistoryTest {

    @Test
    void 사용_결제_내역_셍성시_payment_가_존재하지_않으면_BaseException_예외가_발생(){
        // given
        Long amount = 100_000L;
        User user = User.create("test");

        // when
        BusinessException be = assertThrows( BusinessException.class,()->PointHistory.createUse(amount,user,null) );

        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }

    @Test
    void 결제내역의_결제금액이_0보다작거나_존재하지_않는다면_BaseException_예외가_발생(){
        // given
        Long amount = -1L;
        User user = User.create("test");

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    PointHistory.createCharge(user);
                });
        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }

}