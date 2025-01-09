package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
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
     * USECASE 2.concnertId 로 콘서트 스케줄 정보 페이징해서 가져오기
     * @param command
     * @return
     */
    public ConcertFacadeDto.FindScheduleResult findSchedules(ConcertFacadeDto.FindScheduleParam param){
        // 0. concertId null checking
        // 들어오는 page 은 1 이상 의 자연수

        // 1. 존재하는 콘서트인지 확인
        Concert concert =concertService.findById(param.concertId());


        // 2. 예약 가능한 콘서트 반환 || 예약 가능하고 현재 일짜가 예약가능한 날짜 사이에 있는 스케줄 리턴
        List<ConcertSchedule> concertScheduleList =
                scheduleService.findAvailableSchedules(param.concertId(), (param.page()-1));

        return ConcertFacadeDto.FindScheduleResult.from(concert,concertScheduleList);
    }


    /**
     * USECASE 2.
     * @param concertScheduleId
     * @return
     */
    public ApiResponse<ConcertApiDto.LeftSeatRes> searchAvailableSeats(Long concertScheduleId){

        // 1. 존재하는 콘서트 스케줄인지 확인
        ConcertSchedule concertSchedule = scheduleService.findById(concertScheduleId);

        // 2. 현재 조회 시 예약이 가능한 상태인지 확인
        if(!concertSchedule.getIsReserveAble()){
            // 예약이 마감되었습니다.
        }

        // 3. 예약 가능한 시간대인지 확인
        if(concertSchedule.getReservation_end().isAfter(LocalDateTime.now())){

        }


        // 4. 예약 가능한 콘서트 반환
        // 조회 조건 -> Seat : status -> reservable

        seatService.findReserveAble(concertScheduleId);


        return ApiResponse.ok(ConcertApiDto.LeftSeatRes.builder().build());
    }
}
