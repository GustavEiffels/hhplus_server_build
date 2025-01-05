package kr.hhplus.be.server.persentation.controller.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "1. 유저 대기열 토큰 기능",description = "서비스를 이용할 토큰을 발급받는 API")
@RestController
@RequestMapping("/queue_token/")
public class TokenController {

    @PostMapping("{userId}")
    @Operation(summary = "유저 ",description = "결제 처리하고 결제 내역을 생성하는 API")
    public ResponseEntity<ApiResponse<TokenApiDto.GenerateTokenRes>> createQueueToken(
            @PathVariable("userId")
                    @Schema(description = "사용자 id", example = "12")
            Long userId){
        return new ResponseEntity<>( ApiResponse.ok(
                new TokenApiDto.GenerateTokenRes(12L)
        ), HttpStatus.CREATED);
    }
}
