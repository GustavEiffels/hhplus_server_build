package kr.hhplus.be.server.interfaces.controller.reservation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "3. 좌석 예약 요청 API",description = "좌석 예약을 위한 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {


    @PostMapping("")
    @Operation(summary = "결제 API",description = "날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API ")
    public ResponseEntity<ApiResponse<ReservationApiDto.ReserveDtoRes>> reserve(
            @RequestBody ReservationApiDto.ReserveDtoReq request
    ){
        ReservationApiDto.ReserveDtoRes response = ReservationApiDto.ReserveDtoRes.builder()
                .reservation_id(1)
                .seat_number(14)
                .status(ReservationStatus.Reserved)
                .price(14000)
                .concert_name("서커스!")
                .concert_performer("김광대")
                .expiredAt(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }
}
