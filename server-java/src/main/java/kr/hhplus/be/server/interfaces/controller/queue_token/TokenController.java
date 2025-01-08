package kr.hhplus.be.server.interfaces.controller.queue_token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "1. 유저 대기열 토큰 기능",description = "서비스를 이용할 토큰을 발급받는 API")
@RestController
@RequestMapping("/queue_token")
public class TokenController {

    @PostMapping("")
    @Operation(summary = "유저 ",description = "결제 처리하고 결제 내역을 생성하는 API")
    public ResponseEntity<ApiResponse<TokenApiDto.GenerateTokenRes>> createQueueToken(
            @RequestBody TokenApiDto.GenerateTokenReq request) {

        TokenApiDto.GenerateTokenRes response = TokenApiDto.GenerateTokenRes.builder()
                .tokenId(12L)
                .build();

        return new ResponseEntity<>( ApiResponse.ok(response), HttpStatus.CREATED);
    }
}
