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
        User user = userService.findUser(param.userId());
        return new PointFacadeDto.FindBalanceResult(user.getPoint());
    }

    /**
     * USECASE 4. 잔액 충전 API
     * @param param
     * @return
     */
    @Transactional
    public PointFacadeDto.ChargeResult pointCharge(PointFacadeDto.ChargeParam param){
        // 1. 사용자 조회 후 포인트 업데이트
        User user = userService.chargePoints(param.userId(), param.chargePoint());

        // 2. 사용자 포인트 충전에 대한 이력 생성
        PointHistory history
                = pointHistoryService.create(PointHistory.createCharge(user));

        return PointFacadeDto.ChargeResult.from(history,user);
    }


}
