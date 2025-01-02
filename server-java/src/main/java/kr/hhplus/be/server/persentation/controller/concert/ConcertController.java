
package kr.hhplus.be.server.persentation.controller.concert;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConcertController {

    @PostMapping("/schedules/available-seats")
    public ResponseEntity<ConcertApiDto.LeftSeatRes> findLeftSeat(@RequestBody ConcertApiDto.LeftSeatReq request){
        ConcertApiDto.LeftSeatRes.SeatInfo seat = ConcertApiDto.LeftSeatRes.SeatInfo.builder()
                .seat_num("10")
                .seat_id(1)
                .build();

        ConcertApiDto.LeftSeatRes.ScheduleInfo scheduleInfo = ConcertApiDto.LeftSeatRes.ScheduleInfo.builder()
                .date("2025-03-01T18:00:00")
                .reservation_start("2025-02-01T12:00:00")
                .reservation_end("2025-02-02T12:00:00")
                .build();

        ConcertApiDto.LeftSeatRes response = ConcertApiDto.LeftSeatRes.builder()
                .message("Load All Avaialble Seats")
                .cnt(1)
                .schedule_info(scheduleInfo)
                .seat_list(Arrays.asList(seat))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("schedules")
    public ResponseEntity<ConcertApiDto.AvailableDateRes> findAvailableScheduleDate(@RequestBody ConcertApiDto.AvailableDateReq request) {
        List<ConcertApiDto.AvailableDateRes.ScheduleInfo> scheduleList = Arrays.asList(
                ConcertApiDto.AvailableDateRes.ScheduleInfo.builder()
                        .schedule_id(1)
                        .date("2025-03-01T18:00:00")
                        .reservation_start("2025-02-01T12:00:00")
                        .reservation_end("2025-02-02T12:00:00")
                        .leftTicket(10)
                        .build(),
                ConcertApiDto.AvailableDateRes.ScheduleInfo.builder()
                        .schedule_id(2)
                        .date("2025-03-10T18:00:00")
                        .reservation_start("2025-02-10T12:00:00")
                        .reservation_end("2025-02-12T12:00:00")
                        .leftTicket(30)
                        .build()
        );

        ConcertApiDto.AvailableDateRes response = ConcertApiDto.AvailableDateRes.builder()
                .message("Load All Schedule")
                .cnt(2)
                .concert_info(ConcertApiDto.AvailableDateRes.ConcertInfoDto.builder()
                        .name("서커스!")
                        .performer("김광대")
                        .build())
                .schedule_list(scheduleList)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
