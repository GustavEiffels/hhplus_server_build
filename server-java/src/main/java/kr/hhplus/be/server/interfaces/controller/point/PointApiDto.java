package kr.hhplus.be.server.interfaces.controller.point;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Getter;

public interface PointApiDto {

    record FindBalanceRequest(Long userId) {
        public FindBalanceRequest {
            if (userId == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
        }

        public PointFacadeDto.FindBalanceParam toParam() {
            return new PointFacadeDto.FindBalanceParam(userId);
        }
    }

    record FindBalanceResponse(Long balance) {
        public static FindBalanceResponse from(PointFacadeDto.FindBalanceResult result) {
            return new FindBalanceResponse(result.balance());
        }
    }

    record ChargePointRequest(Long userId, Long chargePoint) {
        public ChargePointRequest {
            if (userId == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
            if (chargePoint == null) {
                throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
            }
        }

        public PointFacadeDto.ChargeParam toParam() {
            return new PointFacadeDto.ChargeParam(userId, chargePoint);
        }
    }

    record ChargePointResponse(String type, Long amount, Long userId) {
        public static ChargePointResponse from(PointFacadeDto.ChargeResult result) {
            return new ChargePointResponse(result.type(), result.amount(), result.userId());
        }
    }

}
