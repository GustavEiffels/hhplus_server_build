package kr.hhplus.be.server.interfaces.controller.point;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. 잔액 충전 / 조회 API",description = "잔액 충전 및 잔액 조회를 위한 API 입니다.")
@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointFacade pointFacade;

    @GetMapping("/point/{userId}")
    @Operation(summary = "조회 API",description = "사용자의 잔액을 조회하는 API")
    public ResponseEntity<ApiResponse<PointApiDto.FindBalanceResponse>> findBalance(
            @PathVariable
            @Schema(description = "사용자 id",example = "1")
            Long userId
    ){
        PointFacadeDto.FindBalanceResult result  = pointFacade.findUserBalance(new PointApiDto.FindBalanceRequest(userId).toParam());
        PointApiDto.FindBalanceResponse response = new PointApiDto.FindBalanceResponse(result.balance());
        return new ResponseEntity<>(ApiResponse.ok(response ), HttpStatus.OK);

    }

    @PutMapping("/point/charge")
    @Operation(summary = "충전 API",description = "사용자의 잔액을 충전하는 API")
    public ResponseEntity<ApiResponse<PointApiDto.ChargePointResponse>> balanceCharge(
            @RequestBody PointApiDto.ChargePointRequest request){

        PointFacadeDto.ChargeResult result = pointFacade.pointCharge(request.toParam());
        PointApiDto.ChargePointResponse response = new PointApiDto.ChargePointResponse(result.type(),result.amount(),result.userId());

        return new ResponseEntity<>(ApiResponse.ok(response ), HttpStatus.OK);
    }
}
