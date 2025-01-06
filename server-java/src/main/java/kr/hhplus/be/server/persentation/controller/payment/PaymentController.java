package kr.hhplus.be.server.persentation.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "5. 결제 API",description = "결제를 위한 컨트롤러")
@RestController
@RequestMapping("/payment")
public class PaymentController {


    @PostMapping("")
    @Operation(summary = "결제 API",description = "결제 처리하고 결제 내역을 생성하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",description = "success")})
    public ResponseEntity<ApiResponse<PaymentApiDto.PurchaseRes>> purchase(
            @RequestBody PaymentApiDto.PurchaseReq request){

        PaymentApiDto.PurchaseRes response = PaymentApiDto.PurchaseRes.builder()
                .message("Purchase Success!")
                .quantity(2)
                .reservation_list(List.of(
                        PaymentApiDto.PurchaseRes.ReservationInfo.builder()
                                .reservation_id(2)
                                .seat_num(14)
                                .concert_name("서커스!")
                                .concert_performer("황광대")
                                .concert_time("2025-03-01T18:00:00")
                                .price(12000)
                                .build(),
                        PaymentApiDto.PurchaseRes.ReservationInfo.builder()
                                .reservation_id(3)
                                .seat_num(18)
                                .concert_name("서커스!")
                                .concert_performer("황광대")
                                .concert_time("2025-03-01T18:00:00")
                                .price(12000)
                                .build()
                ))
                .build();

        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.CREATED);
    }

}
