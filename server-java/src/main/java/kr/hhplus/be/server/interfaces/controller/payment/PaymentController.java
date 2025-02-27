package kr.hhplus.be.server.interfaces.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.PaymentFacadeDto;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "5. 결제 API",description = "결제를 위한 컨트롤러")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentFacade paymentFacade;

    @PostMapping("")
    @Operation(summary = "결제 API",description = "결제 처리하고 결제 내역을 생성하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",description = "success")})
    public ResponseEntity<ApiResponse<PaymentApiDto.PaymentResponse>> purchase(
            @RequestBody PaymentApiDto.PaymentRequest request){

        PaymentFacadeDto.PaymentResult result = paymentFacade.pay(request.toParam());
        PaymentApiDto.PaymentResponse response = PaymentApiDto.PaymentResponse.from(result);

        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

}
