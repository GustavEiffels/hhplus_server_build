package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    @Test
    void 수량이_0보다_작을_겨우_예외가_발샹한다(){
        // given
        int quantity = -1;
        BigDecimal amount = BigDecimal.valueOf(100_000);
        User user = User.builder().name("김연습").build();

        // when
        BusinessException exception = assertThrows(BusinessException.class,()->{
            Payment.builder()
                    .amount(amount)
                    .user(user)
                    .quantity(quantity).build();
        });

        // then
        assertEquals(ErrorCode.Entity,exception.getErrorStatus());
    }

    @Test
    void 결제_금액이_0보다_작을_경우_BusinessException_ErrorCode_Entity_가_발생한다(){
        // given
        int quantity = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);
        User user = User.builder().name("김연습").build();


        // when
        BusinessException exception = assertThrows(BusinessException.class,()->{
            Payment.builder()
                    .amount(amount)
                    .user(user)
                    .quantity(quantity).build();
        });

        // then
        assertEquals(ErrorCode.Entity,exception.getErrorStatus());
    }

    @Test
    void 결제_생성시_사용자_정보가_존재하지_않으면_BusinessException_ErrorCode_Entity_가_발생한다(){
        // given
        int quantity = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);


        // when
        BusinessException exception = assertThrows(BusinessException.class,()->{
            Payment.builder()
                    .amount(amount)
                    .quantity(quantity).build();
        });

        // then
        assertEquals(ErrorCode.Entity,exception.getErrorStatus());
    }

}