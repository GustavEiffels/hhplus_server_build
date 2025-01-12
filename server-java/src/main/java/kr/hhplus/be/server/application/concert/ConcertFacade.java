package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import kr.hhplus.be.server.interfaces.controller.concert.ConcertApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService         concertService ;
    private final ConcertScheduleService scheduleService;
    private final SeatService            seatService    ;

    // 콘서트 아이디로 가능한 콘서트 스케줄 조회
    // 1. concert service
    // 2. concert schedule service

    /**
     * USECASE 2 : concertId 로 콘서트 스케줄 정보 페이징해서 가져오기
     * @param param
     * @return
     */
    public ConcertFacadeDto.FindScheduleResult findSchedules(ConcertFacadeDto.FindScheduleParam param){

        // 1. 콘서트 존재 여부 찾기
        Concert concert = concertService.findConcert(param.concertId());

        // 2. 예약 가능한 콘서트 반환 || 예약 가능하고 현재 일짜가 예약가능한 날짜 사이에 있는 스케줄 리턴
        List<ConcertSchedule> scheduleList =
                scheduleService.findReservableSchedules(param.concertId(), (param.page()-1));

        return ConcertFacadeDto.FindScheduleResult.from(concert,scheduleList);
    }

    /**
     * USECASE 2 : 좌석 API
     * @param param
     * @return
     */
    public ConcertFacadeDto.FindLeftSeatResult findAvailableSeats(ConcertFacadeDto.FindLeftSeatParam param){

        // 1. 예약 가능한 콘서트 스케줄인지 확인
        ConcertSchedule concertSchedule =
                scheduleService.findReservableConcertSchedule(param.scheduleId());

        // 2. 해당 스케줄에 예약 가능한 좌석 반환
        List<Seat> seatList = seatService.findByScheduleId(concertSchedule.getId());

        return ConcertFacadeDto.FindLeftSeatResult.from(seatList);
    }
}
