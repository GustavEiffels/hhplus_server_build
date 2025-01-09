package kr.hhplus.be.server.interfaces.controller.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.payment.PaymentFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public interface PaymentApiDto {
    @Getter
    @Builder
    record PaymentRequest(List<Long> reservationIds, Long userId, Long tokenId) {
        public PaymentRequest {
            if (reservationIds.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "예약 리스트를 보내주세요.");
            }
            if (userId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[userId]는 필수 값입니다.");
            }
            if (tokenId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[tokenId]는 필수 값입니다.");
            }
        }

        // PaymentRequest를 PaymentParam으로 변환하는 메소드
        public PaymentFacadeDto.PaymentParam toParam() {
            return new PaymentFacadeDto.PaymentParam(reservationIds, userId, tokenId);
        }
    }

    // 결제 결과 응답 (Response)
    @Getter
    @Builder
    record PaymentResponse(List<PaymentInfo> paymentInfoList) {
        public static PaymentResponse from(PaymentFacadeDto.PaymentResult result) {
            List<PaymentInfo> paymentInfoList = result.paymentInfoList().stream()
                    .map(payment -> new PaymentInfo(
                            payment.paymentId(),
                            payment.reservationId(),
                            payment.amount()))
                    .collect(Collectors.toList());
            return new PaymentResponse(paymentInfoList);
        }
    }

    // 결제 정보 (단일 응답 항목)
    @Getter
    @Builder
    record PaymentInfo(Long paymentId, Long reservationId, Long amount) {}
}
