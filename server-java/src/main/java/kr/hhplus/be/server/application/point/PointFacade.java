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


    public ApiResponse<PointApiDto.FindBalanceRes> findUserBalance(Long userId){
        User user = userService.find(userId);
        return ApiResponse.ok(PointApiDto.FindBalanceRes.builder().point(user.getPoint()).build());
    }

    @Transactional
    public ApiResponse<PointApiDto.BalanceChargeRes> pointCharge(PointApiDto.BalanceChargeReq request){
        User user = userService.find(request.getUserId());

        user.pointTransaction(request.getPoint());

        return  ApiResponse.ok(PointApiDto.BalanceChargeRes
                .builder()
                .point(request.getPoint())
                .build());
    }


}
