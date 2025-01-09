package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.user.User;

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

    record ChargeParam(Long userId,Long chargePoint){
        public ChargeParam{
            if(userId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[사용자 아이디]는 필수값 입니다.");
            }
            if(chargePoint == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[포인트]는 필수값 입니다.");
            }
        }
    }

    record ChargeResult(String type, Long amount, Long userId) {
        public static ChargeResult from(PointHistory pointHistory, User user) {
            String type = pointHistory.getStatus().name();
            Long amount = pointHistory.getAmount();
            Long userId = user.getId();

            return new ChargeResult(type, amount, userId);
        }
    }


}
