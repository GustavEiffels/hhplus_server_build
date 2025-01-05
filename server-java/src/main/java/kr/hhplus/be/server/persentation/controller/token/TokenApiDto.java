package kr.hhplus.be.server.persentation.controller.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public interface TokenApiDto {
    @Getter
    class GenerateTokenRes{

        @Schema(description = "토큰 id",example = "2")
        private Long   tokenId;

        public GenerateTokenRes(Long tokenId){
            this.tokenId = tokenId;
        }
    }
}
