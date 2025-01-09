
package kr.hhplus.be.server.interfaces.controller.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.concert.ConcertFacade;
import kr.hhplus.be.server.application.concert.ConcertFacadeDto;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "2. 예약 가능 날짜 / 좌석 API",description = "예약 가능한 날짜/좌석을 조회하기 위한 컨트롤러")
@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    @GetMapping("/{schedule_id}/available-seat")
    @Operation( summary = "예약 가능 좌석 API", description = "날짜 정보를 입력받아 예약가능한 좌석정보를 조회" )
    public ResponseEntity<ApiResponse<ConcertApiDto.FindLeftSeatResponse>> findLeftSeat(@PathVariable("schedule_id") Long schedule_id ){
        ConcertApiDto.FindLeftSeatRequest request   = new ConcertApiDto.FindLeftSeatRequest(schedule_id);
        ConcertApiDto.FindLeftSeatResponse response = ConcertApiDto.FindLeftSeatResponse.from(concertFacade.findAvailableSeats(request.toParam()));
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }


    @GetMapping("/{concertId}/{page}")
    @Operation(summary = "예약 가능 날짜",description = "예약 가능한 날짜 목록을 조회")
    public ResponseEntity<ApiResponse<ConcertApiDto.FindScheduleResponse>> findSchedules(
            @PathVariable("concertId") Long concertId, @PathVariable("page") int page) {

        return new ResponseEntity<>(
                ApiResponse.ok(
                        concertFacade.findSchedules(
                                new ConcertApiDto.FindScheduleRequest(concertId,page)
                                        .to())
                                .toResponse()), HttpStatus.OK);
    }


}
