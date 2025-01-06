package kr.hhplus.be.server.persentation.controller.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. 잔액 충전 / 조회 API",description = "잔액 충전 및 잔액 조회를 위한 API 입니다.")
@RestController
public class UserController {

    @GetMapping("/point/{userId}")
    @Operation(summary = "조회 API",description = "사용자의 잔액을 조회하는 API")
    public ResponseEntity<ApiResponse<UserApiDto.FindBalanceRes>> findBalance(
            @PathVariable
            @Schema(description = "사용자 id",example = "1")
            Long userId
    ){
        return new ResponseEntity<>(ApiResponse.ok(
                UserApiDto.FindBalanceRes.builder()
                .point(12000).build()), HttpStatus.OK);
    }

    @PutMapping("/point/charge")
    @Operation(summary = "충전 API",description = "사용자의 잔액을 충전하는 API")
    public ResponseEntity<UserApiDto.BalanceChargeRes> balanceCharge(
            @RequestBody UserApiDto.BalanceChargeReq request){
        return new ResponseEntity<>(UserApiDto.BalanceChargeRes.builder()
                .message("충전이 완료 되었습니다.")
                .point(14000)
                .build(), HttpStatus.OK);
    }
}
