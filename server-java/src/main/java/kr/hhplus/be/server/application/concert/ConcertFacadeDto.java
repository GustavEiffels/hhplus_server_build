package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.interfaces.controller.concert.ConcertApiDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface ConcertFacadeDto {

    record FindScheduleParam(Long concertId, Integer page){
        public FindScheduleParam{
            if(page<1){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[page]는 자연수만 허용합니다.");
            }
            if(concertId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[concertId]는 필수 값 입니다.");
            }
        }
    }

    record FindScheduleResult(String title, String performer, List<ScheduleInfo> scheduleInfoList){
        public static FindScheduleResult from(Concert concert, List<ConcertSchedule> scheduleList) {
            List<ScheduleInfo> scheduleInfolist = scheduleList.stream()
                    .map(schedule -> new ScheduleInfo(
                            schedule.getReservation_start(),
                            schedule.getReservation_end(),
                            schedule.getShowTime()))
                    .toList();

            return new FindScheduleResult(
                    concert.getTitle(),
                    concert.getPerformer(),
                    scheduleInfolist
            );
        }
        public ConcertApiDto.FindScheduleResponse toResponse() {
            List<ConcertApiDto.ScheduleInfo> apiScheduleList = this.scheduleInfoList.stream()
                    .map(info -> new ConcertApiDto.ScheduleInfo(info.re_start(), info.re_end(), info.show()))
                    .toList();

            return new ConcertApiDto.FindScheduleResponse(this.title, this.performer, apiScheduleList);
        }
    }
    record ScheduleInfo(LocalDateTime re_start,LocalDateTime re_end,LocalDateTime show){}




    record FindLeftSeatParam(Long scheduleId){
        public FindLeftSeatParam{
            if(scheduleId == null){
                throw new BusinessException(ErrorCode.INVALID_INPUT,"[scheduleId]는 필수 값 입니다.");
            }
        }
    }


    record FindLeftSeatResult(List<SeatInfo> seatInfoList){
        public static FindLeftSeatResult from(List<Seat> seatList){
            List<SeatInfo> seatInfoList = seatList.stream()
                    .map(seat -> new SeatInfo(seat.getSeatNo(), seat.getId(), seat.getPrice()))
                    .collect(Collectors.toList());
            return new FindLeftSeatResult(seatInfoList);
        }
    }

    record SeatInfo(int seatNo, Long seatId, Long price){}
}


