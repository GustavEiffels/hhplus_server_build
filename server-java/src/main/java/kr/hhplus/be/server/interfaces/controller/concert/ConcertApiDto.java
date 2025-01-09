package kr.hhplus.be.server.interfaces.controller.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.concert.ConcertFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


public interface ConcertApiDto {

    record FindScheduleRequest(Long concertId, Integer page) {
        public FindScheduleRequest {
            if (page == null || page < 1) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[page]는 1 이상의 자연수만 허용합니다.");
            }
            if (concertId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[concertId]는 필수 값 입니다.");
            }
        }
        public ConcertFacadeDto.FindScheduleParam to() {
            return new ConcertFacadeDto.FindScheduleParam(this.concertId, this.page);
        }
    }
    record FindScheduleResponse(String title, String performer, List<ScheduleInfo> scheduleInfoList){}

    record ScheduleInfo(LocalDateTime re_start,LocalDateTime re_end,LocalDateTime show){}




    record FindLeftSeatRequest(Long scheduleId) {
        public FindLeftSeatRequest {
            if (scheduleId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "[scheduleId]는 필수 값 입니다.");
            }
        }

        public ConcertFacadeDto.FindLeftSeatParam toParam() {
            return new ConcertFacadeDto.FindLeftSeatParam(scheduleId);
        }
    }

    record FindLeftSeatResponse(List<SeatInfo> seatInfoList) {
        public static FindLeftSeatResponse from(ConcertFacadeDto.FindLeftSeatResult result) {
            List<SeatInfo> seatInfoList = result.seatInfoList().stream()
                    .map(seat -> new SeatInfo(seat.seatNo(), seat.seatId(), seat.price()))
                    .toList();
            return new FindLeftSeatResponse(seatInfoList);
        }

        record SeatInfo(int seatNo, Long seatId, Long price) {}
    }

}
