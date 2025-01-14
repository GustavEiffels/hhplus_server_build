package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("사용자 생성 시, 이름을 넣지 않으면 ErrorCode : REQUIRE_FIELD_MISSING 가 발생한다.")
    @Test
    void user_create_test00(){
        // given & when
        BusinessException exception = Assertions.assertThrows(BusinessException.class,()-> User.create("test"));

        // then
        Assertions.assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus(),"REQUIRE_FIELD_MISSING 예외가 발생 ");
    }


    @DisplayName("한 사용자의 거래 이후 포인트가, 최대 포인트량을 넘을 경우 ErrorCode : MAXIMUM_POINT_EXCEEDED 가 발생한다.")
    @Test
    void user_pointTransaction_test00(){
        // given
        int transactionPoint = 100_000_001;
        User testUser = User.create("test");

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class,()-> testUser.pointTransaction(transactionPoint));

        // then
        Assertions.assertEquals(ErrorCode.MAXIMUM_POINT_EXCEEDED,exception.getErrorStatus(),"MAXIMUM_POINT_EXCEEDED 예외가 발생 ");
    }


    @DisplayName("한 사용자의 거래 이후 포인트가, 최소 포인트량 보다 적은 경우 ErrorCode : INSUFFICIENT BALANCE 가 발생한다.")
    @Test
    void user_pointTransaction_test01(){
        // given
        long transactionPoint = -1L;
        User testUser = User.create("test");

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class,()-> testUser.pointTransaction(transactionPoint));

        // then
        Assertions.assertEquals(ErrorCode.INSUFFICIENT_BALANCE,exception.getErrorStatus(),"INSUFFICIENT_BALANCE 예외가 발생 ");
    }

}