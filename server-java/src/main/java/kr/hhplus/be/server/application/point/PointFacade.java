package kr.hhplus.be.server.application.point;


import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import kr.hhplus.be.server.persentation.controller.point.PointApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;

    /**
     * USECASE 4. 잔액 조회 API
     * @param userId
     * @return
     */
    public ApiResponse<PointApiDto.FindBalanceRes> findUserBalance(Long userId){
        // 1.사용자 조회
        User user = userService.find(userId);
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
        User user = userService.find(request.getUserId());

        // 2. 사용자 포인트 거래
        user.pointTransaction(request.getPoint());

        return  ApiResponse.ok(PointApiDto.BalanceChargeRes
                .builder()
                .point(request.getPoint())
                .build());
    }


}
