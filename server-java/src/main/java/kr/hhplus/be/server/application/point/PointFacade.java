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
     * @param userId
     * @return
     */
    public ApiResponse<PointApiDto.FindBalanceRes> findUserBalance(Long userId){
        // 1.사용자 조회
        User user = userService.findById(userId);
        return ApiResponse.ok(PointApiDto.FindBalanceRes.builder().point(user.getPoint()).build());
    }

    /**
     * USECASE 4. 잔액 충전 API
     * @param request
     * @return
     */
    @Transactional
    public ApiResponse<PointApiDto.BalanceChargeRes> pointCharge(PointApiDto.BalanceChargeReq request){
        // 1. 사용자 조회
        User user = userService.findById(request.getUserId());

        // 2. 사용자 포인트 거래
        user.pointTransaction(request.getPoint());

        // 3. Point 충전 이력 생성
        pointHistoryService.create(PointHistory.createCharge(request.getPoint(),user));

        return  ApiResponse.ok(PointApiDto.BalanceChargeRes
                .builder()
                .point(request.getPoint())
                .build());
    }


}
