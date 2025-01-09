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
    @Getter
    @Builder
    record ReservationRequest(Long scheduleId, List<Long> seatIdList, Long userId) {
        public ReservationRequest {
            if (scheduleId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[scheduleId]는 필수 값입니다.");
            }
            if (seatIdList.size() > 4) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "한번에 예약 가능한 좌석수는 최대 4개 까지입니다.");
            }
            if (seatIdList.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "예약할 좌석을 입력해주세요.");
            }
            if (userId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[사용자 아이디]는 필수 값입니다.");
            }
        }

        // ReservationRequest를 ReservationParam으로 변환하는 메소드
        public ReservationFacadeDto.ReservationParam toParam() {
            return new ReservationFacadeDto.ReservationParam(scheduleId, seatIdList, userId);
        }
    }

    // 예약 결과 응답 (Response)
    @Getter
    @Builder
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
    @Getter
    @Builder
    record ReservationInfo(Long reservationId, Long seatId, Long amount) {}
}
