package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import kr.hhplus.be.server.persentation.controller.concert.ConcertApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final ConcertScheduleService scheduleService;

    // 콘서트 아이디로 가능한 콘서트 스케줄 조회
    // 1. concert service
    // 2. concert schedule service
    public ApiResponse<ConcertApiDto.AvailableDateRes> searchAvailableSchedule(Long concertId, int page){
        // 1. 존재하는 콘서트인지 확인
        concertService.find(concertId);


        // 2. 예약 가능한 콘서트 반환 || leftTicket 이 0 보다 크고, 현재 일짜가 예약가능한 날짜 사이에 있는 스케줄 리턴
        scheduleService.findReservableScheduleList(concertId,(page-1));


        return ApiResponse.ok(ConcertApiDto.AvailableDateRes.builder().build());
    }
}
