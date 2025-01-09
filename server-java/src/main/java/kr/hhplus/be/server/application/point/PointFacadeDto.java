package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;

public interface PointFacadeDto {

    // PARAM
    record FindBalanceParam(Long userId){
        public FindBalanceParam{
            if(userId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[사용자 아이디]는 필수값 입니다.");
            }
        }
    }

    // RESULT
    record FindBalanceResult(Long balance){
        public FindBalanceResult{
            if(balance == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[잔액]은 필수로 입력되어야합니다. ");
            }
        }
    }

}
