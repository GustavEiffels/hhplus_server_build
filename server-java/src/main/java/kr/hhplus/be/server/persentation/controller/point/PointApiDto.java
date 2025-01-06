package kr.hhplus.be.server.persentation.controller.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public interface PointApiDto {

    @Getter
    @Builder
    class FindBalanceRes{
        @Schema(description = "사용자의 잔액",example = "100000")
        private int point;
    }
    class FindBalanceReq{
    }

    @Getter
    @Builder
    class BalanceChargeReq{
        @Schema(description = "충전할 금액",example = "100000")
        private int point;

        @Schema(description = "사용자 id",example = "1")
        private Long userId;
    }

    @Getter
    @Builder
    class BalanceChargeRes{
        @Schema(description = "충전 이후 메세지",example = "충전이 완료 되었습니다.")
        private String message;
        @Schema(description = "충전 이후 잔액",example = "200000")
        private int point;
    }
}
