package kr.hhplus.be.server.persentation.controller.reservation;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {


    @PostMapping("/purchase")
    public ResponseEntity<ReservationApiDto.PurchaseRes> purchase(
            @RequestBody ReservationApiDto.PurchaseReq request){
        ReservationApiDto.PurchaseRes response = ReservationApiDto.PurchaseRes.builder()
                .message("Purchase Success!")
                .quantity(2)
                .reservation_list(List.of(
                        ReservationApiDto.PurchaseRes.ReservationInfo.builder()
                                .reservation_id(2)
                                .seat_num(14)
                                .concert_name("서커스!")
                                .concert_performer("황광대")
                                .concert_time("2025-03-01T18:00:00")
                                .price(12000)
                                .build(),
                        ReservationApiDto.PurchaseRes.ReservationInfo.builder()
                                .reservation_id(3)
                                .seat_num(18)
                                .concert_name("서커스!")
                                .concert_performer("황광대")
                                .concert_time("2025-03-01T18:00:00")
                                .price(12000)
                                .build()
                ))
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<ReservationApiDto.ReserveDtoRes> reserve(
            @RequestBody ReservationApiDto.ReserveDtoReq request
    ){
        ReservationApiDto.ReserveDtoRes response = ReservationApiDto.ReserveDtoRes.builder()
                .message("The Seat is occupied.")
                .reserved_info(ReservationApiDto.ReserveDtoRes.ReservedInfo.builder()
                        .reservation_id(1)
                        .seat_number(14)
                        .status("occupied")
                        .price(14000)
                        .concert_name("서커스!")
                        .concert_performer("김광대")
                        .expiredAt("2025-01-03T10:00:00")
                        .build())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
