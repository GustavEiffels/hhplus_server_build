package kr.hhplus.be.server.application.point;


import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.point_history.PointHistoryService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import kr.hhplus.be.server.interfaces.controller.point.PointApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointHistoryService pointHistoryService;

    /**
     * USECASE 4. 잔액 조회 API
     * @param param
     * @return
     */
    public PointFacadeDto.FindBalanceResult findUserBalance(PointFacadeDto.FindBalanceParam param){
        // 1.사용자 조회
        User user = userService.findById(param.userId());
        return new PointFacadeDto.FindBalanceResult(user.getPoint());
    }

    /**
     * USECASE 4. 잔액 충전 API
     * @param param
     * @return
     */
    @Transactional
    public PointFacadeDto.ChargeResult pointCharge(PointFacadeDto.ChargeParam param){
        // 1. 사용자 조회
        User user = userService.findByIdWithLock(param.userId());

        // 2. 사용자 포인트 거래
        user.pointTransaction(param.chargePoint());

        // 3. Point 충전 이력 생성
        PointHistory history
                = pointHistoryService.create(PointHistory.createCharge(param.chargePoint(),user));

        return PointFacadeDto.ChargeResult.from(history,user);
    }


}
