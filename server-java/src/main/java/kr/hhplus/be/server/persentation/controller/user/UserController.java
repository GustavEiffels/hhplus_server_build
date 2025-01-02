package kr.hhplus.be.server.persentation.controller.user;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{userId}/point")
    public ResponseEntity<UserApiDto.FindBalanceRes> findBalance(
            @PathVariable Long userId){
        return new ResponseEntity<>(UserApiDto.FindBalanceRes.builder()
                .point(12000).build(), HttpStatus.OK);
    }

    @PostMapping("/{userId}/charge")
    public ResponseEntity<UserApiDto.BalanceChargeRes> balanceCharge(
            @PathVariable Long userId,
            @RequestBody UserApiDto.BalanceChargeReq request){
        return new ResponseEntity<>(UserApiDto.BalanceChargeRes.builder()
                .message("충전이 완료 되었습니다.")
                .point(14000)
                .build(), HttpStatus.OK);
    }
}
