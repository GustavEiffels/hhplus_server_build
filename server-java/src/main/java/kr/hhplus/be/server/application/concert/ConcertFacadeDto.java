package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;

public interface ConcertFacadeDto {

    record AvailableSchedulesCommand(Long concertId, Integer page){
        public AvailableSchedulesCommand{
            if(page<1){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[page]는 자연수만 허용합니다.");
            }
            if(concertId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[concertId]는 필수 값 입니다.");
            }
        }
    }
}
