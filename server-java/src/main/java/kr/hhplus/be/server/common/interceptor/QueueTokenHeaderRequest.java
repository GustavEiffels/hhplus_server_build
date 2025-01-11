package kr.hhplus.be.server.common.interceptor;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;

public record QueueTokenHeaderRequest(Long queueTokenId, Long userId) {

    public QueueTokenHeaderRequest {
    }

    public QueueTokenHeaderRequest(String queueTokenHeader, String userIdHeader) {
        this(parseQueueToken(queueTokenHeader), parseUserId(userIdHeader));
    }

    private static Long parseQueueToken(String queueTokenHeader) {
        if (queueTokenHeader == null || queueTokenHeader.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Queue_Token header 는 필수 입니다.");
        }
        try {
            return Long.parseLong(queueTokenHeader);
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
