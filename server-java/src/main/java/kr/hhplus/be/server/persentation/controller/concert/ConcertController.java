
package kr.hhplus.be.server.persentation.controller.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "2. 예약 가능 날짜 / 좌석 API",description = "예약 가능한 날짜/좌석을 조회하기 위한 컨트롤러")
@RestController
@RequestMapping("/concerts")
public class ConcertController {

    // 임시 테스트
    @GetMapping("")
    public String test(){
        return null;
    }

    @GetMapping("/{schedules}/available-seat")
    @Operation( summary = "예약 가능 좌석 API", description = "날짜 정보를 입력받아 예약가능한 좌석정보를 조회" )
    public ResponseEntity<ApiResponse<ConcertApiDto.LeftSeatRes>> findLeftSeat(@PathVariable("schedules") Long schedules ){
        ConcertApiDto.LeftSeatRes.SeatInfo seat = ConcertApiDto.LeftSeatRes.SeatInfo.builder()
                .seat_num("10")
                .seat_id(1)
                .build();

        ConcertApiDto.LeftSeatRes response = ConcertApiDto.LeftSeatRes.builder()
                .cnt(1)
                .date(LocalDateTime.now().plusDays(10))
                .reservation_start(LocalDateTime.now().plusDays(1))
                .reservation_end(LocalDateTime.now().plusDays(2))
                .seat_list(Arrays.asList(seat))
                .build();

        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }


    @GetMapping("/{concertId}")
    @Operation(summary = "예약 가능 날짜",description = "예약 가능한 날짜 목록을 조회")
    public ResponseEntity<ApiResponse<ConcertApiDto.AvailableDateRes>> findAvailableScheduleDate(@PathVariable("concertId") Long concertId) {
        List<ConcertApiDto.AvailableDateRes.ScheduleInfo> scheduleList = Arrays.asList(
                ConcertApiDto.AvailableDateRes.ScheduleInfo.builder()
                        .schedule_id(1)
                        .date(LocalDateTime.now().plusDays(10))
                        .reservation_start(LocalDateTime.now().plusDays(1))
                        .reservation_end(LocalDateTime.now().plusDays(2))
                        .leftTicket(10)
                        .build(),
                ConcertApiDto.AvailableDateRes.ScheduleInfo.builder()
                        .schedule_id(2)
                        .date(LocalDateTime.now().plusDays(10))
                        .reservation_start(LocalDateTime.now().plusDays(1))
                        .reservation_end(LocalDateTime.now().plusDays(2))
                        .leftTicket(30)
                        .build()
        );

        ConcertApiDto.AvailableDateRes response = ConcertApiDto.AvailableDateRes.builder()
                .cnt(2)
                .concert_info(ConcertApiDto.AvailableDateRes.ConcertInfoDto.builder()
                        .name("서커스!")
                        .performer("김광대")
                        .build())
                .schedule_list(scheduleList)
                .build();

        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }


}
