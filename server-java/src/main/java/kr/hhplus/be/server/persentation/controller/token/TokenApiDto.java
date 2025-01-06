package kr.hhplus.be.server.persentation.controller.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public interface TokenApiDto {

    @Getter
    @Builder
    class GenerateTokenReq{
        @Schema(description = "사용자 id",example = "12")
        private Long userId;
    }


    @Getter
    @Builder
    class GenerateTokenRes{

        @Schema(description = "토큰 id",example = "2")
        private Long   tokenId;

    }
}
