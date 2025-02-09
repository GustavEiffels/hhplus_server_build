package kr.hhplus.be.server.application.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;

public interface QueueTokenFacadeDto {

    record CreateParam(Long userId){
        public CreateParam{
            if(userId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[사용자 아이디]는 필수 값이다.");
            }
        }
    }

    record CreateResult(String tokenId){}


    record ActiveCheckParam(String tokenId, Long userId) {
        public ActiveCheckParam {
        }

        public ActiveCheckParam(String queueTokenHeader, String userIdHeader) {
            this(parseQueueToken(queueTokenHeader), parseUserId(userIdHeader));
        }

        private static String parseQueueToken(String queueTokenHeader) {
            if (queueTokenHeader == null || queueTokenHeader.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "Queue_Token header 는 필수 입니다.");
            }
            try {
                return queueTokenHeader;
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "Queue_Token 형식이 맞지 않습니다.");
            }
        }

        private static Long parseUserId(String userIdHeader) {
            if (userIdHeader == null || userIdHeader.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "UserId header 는 필수 입니다.");
            }
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "UserId 형식이 맞지 않습니다.");

            }

        }
    }
    record ActiveCheckResult(Boolean isActive){}


    record ActivateParam(Long maxTokenCnt){
        public ActivateParam{
            if(maxTokenCnt == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[최대 토큰 수]는 필수 값이다.");
            }
            if(maxTokenCnt < 10 ){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[최대 토큰 수]는 10 이상의 자연수여야 합니다.");
            }
        }
    }
}
