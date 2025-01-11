package kr.hhplus.be.server.interfaces.controller.reservation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFacade reservationFacade;

    @PostMapping("")
    @Operation(summary = "결제 API",description = "날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API ")
    public ResponseEntity<ApiResponse<ReservationApiDto.ReservationResponse>> reserve(
            @RequestBody ReservationApiDto.ReservationRequest request
    ){
        ReservationFacadeDto.ReservationResult result = reservationFacade.reservation(request.toParam());
        ReservationApiDto.ReservationResponse response = ReservationApiDto.ReservationResponse.from(result);
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }
}
