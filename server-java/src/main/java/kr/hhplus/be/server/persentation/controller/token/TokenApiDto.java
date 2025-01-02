package kr.hhplus.be.server.persentation.controller.token;

import lombok.Builder;
import lombok.Getter;

public interface TokenApiDto {
    @Getter
    @Builder
    class GenerateTokenRes{
        private String message;
        private Long   tokenId;

        public GenerateTokenRes(Long tokenId, String message){
            this.tokenId = tokenId;
            this.message = message;
        }
    }
}
