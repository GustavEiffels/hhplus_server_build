package kr.hhplus.be.server.interfaces.controller.reservation;

import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

public interface ReservationApiDto {

    // 예약 요청 파라미터 (Request)
    record ReservationRequest(Long scheduleId, List<Long> seatIdList, Long userId) {
        public ReservationRequest {
            if (scheduleId == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
            if (seatIdList.size() > 4) {
                throw new BusinessException(ErrorCode.EXCEEDED_MAX_RESERVATION_ONCE);
            }
            if (seatIdList.isEmpty()) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
            if (userId == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
        }

        // ReservationRequest를 ReservationParam으로 변환하는 메소드
        public ReservationFacadeDto.ReservationParam toParam() {
            return new ReservationFacadeDto.ReservationParam(scheduleId, seatIdList, userId);
        }
    }

    // 예약 결과 응답 (Response)
    record ReservationResponse(List<ReservationInfo> reservationInfoList) {
        public static ReservationResponse from(ReservationFacadeDto.ReservationResult result) {
            List<ReservationInfo> reservationInfoList = result.reservationInfoList().stream()
                    .map(reservation -> new ReservationInfo(
                            reservation.reservationId(),
                            reservation.seatId(),
                            reservation.amount()))
                    .collect(Collectors.toList());
            return new ReservationResponse(reservationInfoList);
        }
    }

    // 개별 예약 정보 (단일 응답 항목)
    record ReservationInfo(Long reservationId, Long seatId, Long amount) {}
}
