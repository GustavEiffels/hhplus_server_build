package kr.hhplus.be.server.persentation.controller.user;

import lombok.Builder;
import lombok.Getter;

public interface UserApiDto {

    @Getter
    @Builder
    class FindBalanceRes{
        private int point;
    }
    class FindBalanceReq{

    }

    @Getter
    @Builder
    class BalanceChargeReq{
        private int point;
    }
    @Getter
    @Builder
    class BalanceChargeRes{
        private String message;
        private int point;
    }
}
