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
        BigDecimal amount = BigDecimal.valueOf(100_000);
        User user = User.builder().name("김연습").build();

        // when
        BusinessException be = assertThrows( BusinessException.class,()->PointHistory.createUse(amount,user,null) );

        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }

    @Test
    void 결제내역의_결제금액이_0보다작거나_존재하지_않는다면_BaseException_예외가_발생(){
        // given
        BigDecimal amount = BigDecimal.valueOf(-1);
        User user = User.builder().name("김연습").build();

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    PointHistory.createCharge(amount,user);
                });
        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }


    @Test
    void 결제내역의_결제금액이_사용자_정보가_존재하지_않는다면_BaseException_예외가_발생(){
        // given
        BigDecimal amount = BigDecimal.valueOf(-1);

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    PointHistory.createCharge(amount,null);
                });
        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }
}