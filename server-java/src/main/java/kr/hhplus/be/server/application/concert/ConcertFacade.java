package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.persentation.controller.ApiResponse;
import kr.hhplus.be.server.persentation.controller.concert.ConcertApiDto;
import org.springframework.stereotype.Service;

@Service
public class ConcertFacade {

    // 콘서트 아이디로 가능한 콘서트 스케줄 조회
    public ApiResponse<ConcertApiDto.AvailableDateRes> searchAvailableSchedule(Long concertId){
        

        return ApiResponse.ok(ConcertApiDto.AvailableDateRes.builder().build());
    }
}
