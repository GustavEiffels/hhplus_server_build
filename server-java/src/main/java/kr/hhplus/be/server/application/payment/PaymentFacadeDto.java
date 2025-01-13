package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.payment.Payment;
import java.util.List;
import java.util.stream.Collectors;

public interface PaymentFacadeDto {
    record PaymentParam(List<Long> reservationIds, Long userid, Long tokenId){
        public PaymentParam{
            if( reservationIds.isEmpty() ){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"예약 리스트를 보내주세요");
            }
            if( userid == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[userid]는  필수 값입니다.");
            }
            if( tokenId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[tokenId]는  필수 값입니다.");
            }
        }
    }

    record PaymentResult(List<PaymentInfo> paymentInfoList){
        public static PaymentResult from(List<Payment> paymentList) {
            List<PaymentInfo> paymentInfoList = paymentList.stream()
                    .map(payment -> new PaymentInfo(
                            payment.getId(),
                            payment.getId(),
                            payment.getAmount()
                    ))
                    .collect(Collectors.toList());
            return new PaymentResult(paymentInfoList);
        }
    }
    record PaymentInfo(Long paymentId, Long reservationId, Long amount){}
}
