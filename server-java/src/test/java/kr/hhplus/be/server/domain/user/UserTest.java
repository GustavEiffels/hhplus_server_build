package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void 사용자의_포인트_거래량이_100_000_000을_초과하는_경우_BaseException_예외가_발생(){
        // given : 사용자를 생성하고, 사용자가 거래하는 포인트량을 적절하지 않은 포인트로 설정한다.
        int transactionPoint = 100_000_001;
        User testUser = User.builder().name("김연습").build();

        // when : 포인트 트랜잭션을 수행하면
        BusinessException exception = Assertions.assertThrows(BusinessException.class,()->{
            testUser.pointTransaction(transactionPoint);
        });

        // then : ErrorCode 의 Status 가 "Entity" 인 에러가 발생한다.
        Assertions.assertEquals(ErrorCode.Entity,exception.getErrorStatus());
    }

}