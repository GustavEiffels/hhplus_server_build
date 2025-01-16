package kr.hhplus.be.server.interfaces.controller.queue_token;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Getter;

public interface QueueTokenApiDto {

    record CreateTokenRequest(Long userId) {
        public CreateTokenRequest {
            if (userId == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
        }

        public QueueTokenFacadeDto.CreateParam toParam() {
            return new QueueTokenFacadeDto.CreateParam(userId);
        }
    }

    record CreateTokenResponse(Long tokenId) {
        public static CreateTokenResponse from(QueueTokenFacadeDto.CreateResult result) {
            return new CreateTokenResponse(result.tokenId());
        }
    }

    record CheckActiveTokenRequest(String queueTokenHeader, String userIdHeader) {
        public QueueTokenFacadeDto.ActiveCheckParam toParam() {
            return new QueueTokenFacadeDto.ActiveCheckParam(queueTokenHeader, userIdHeader);
        }
    }

    record CheckActiveTokenResponse(Boolean isActive) {
        public static CheckActiveTokenResponse from(QueueTokenFacadeDto.ActiveCheckResult result) {
            return new CheckActiveTokenResponse(result.isActive());
        }
    }

    @Getter
    class ActivateTokenRequest {
        private  Long maxTokenCnt;
        public ActivateTokenRequest(Long maxTokenCnt) {
            if (maxTokenCnt == null || maxTokenCnt < 10) {
                maxTokenCnt = 20L;
            }
            this.maxTokenCnt = maxTokenCnt;
        }

        public QueueTokenFacadeDto.ActivateParam toParam() {
            return new QueueTokenFacadeDto.ActivateParam(maxTokenCnt);
        }
    }

    record ActivateTokenResponse(String message) {
        public static ActivateTokenResponse success() {
            return new ActivateTokenResponse("토큰 활성화가 완료되었습니다.");
        }
    }

}
