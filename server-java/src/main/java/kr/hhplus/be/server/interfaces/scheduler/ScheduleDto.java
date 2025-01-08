package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;

public interface ScheduleDto {

    record ActiveTokenRequest(long maxToken) {
        public ActiveTokenRequest {
            if (maxToken < 5) {
                throw new BusinessException(ErrorCode.INVALID_INPUT,
                        "활성화 토큰의 최대 개수는 5이상의 자연수 입니다.");
            }
        }
    }
}
