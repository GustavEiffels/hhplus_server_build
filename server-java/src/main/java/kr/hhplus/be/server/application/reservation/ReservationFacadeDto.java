package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.reservation.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public interface ReservationFacadeDto {

    record ReservationParam(Long scheduleId, List<Long> seatIdList, Long userId){
        public ReservationParam{
            if(scheduleId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[scheduleId]는  필수 값입니다.");
            }
            if(seatIdList.size() > 4 ){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"한번에 예약 가능한 좌석수는  최대 4개 까지이다.");
            }
            if(seatIdList.isEmpty()){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"예약할 좌석을 입력해주세요");
            }
            if(userId == null ){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[사용자 아이디] 는 필수 값입니다.");
            }
        }
    }


    record ReservationResult(List<ReservationInfo> reservationInfoList){
        public static ReservationResult from(List<Reservation> reservations){
            List<ReservationInfo> reservationInfoList = reservations.stream()
                    .map(reservation -> new ReservationInfo(
                            reservation.getId(),
                            reservation.getSeat().getId(),
                            reservation.getAmount()))
                    .collect(Collectors.toList());

            // ReservationResult로 반환
            return new ReservationResult(reservationInfoList);
        }
    }
    record ReservationInfo(Long reservationId, Long seatId, Long amount){}
}
