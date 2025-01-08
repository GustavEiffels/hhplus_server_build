package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.common.interceptor.QueueTokenHeaderRequest;
import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import kr.hhplus.be.server.persentation.controller.queue_token.TokenApiDto;
import kr.hhplus.be.server.persentation.scheduler.ScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueTokenFacade {

    private final UserService userService;
    private final QueueTokenService queueTokenService;


    /**
     * USECASE : 1. 유저 대기열 토큰 발급 받는 API
     * @param request
     * @return
     */
    @Transactional
    public ApiResponse<TokenApiDto.GenerateTokenRes> create(TokenApiDto.GenerateTokenReq request){
        // 1. find user
        User user = userService.findById(request.getUserId());

        // 2. Create Entity & Create
        QueueToken newQueueToken = queueTokenService.createToken(
                QueueToken.builder().user(user).build());

        return ApiResponse.ok(TokenApiDto.GenerateTokenRes.builder().tokenId(newQueueToken.getId()).build());
    }

    /**
     * 인터셉터
     * @param queueTokenId
     * @param userId
     * @return
     */
    public Boolean isValidToken(QueueTokenHeaderRequest request){
        // 1. find user
        userService.findById(request.userId());

        // 2. isValid Token
        return queueTokenService.isValidAndActive(request.queueTokenId(), request.userId());
    }


    @Transactional
    public void activate(ScheduleDto.ActiveTokenRequest request){
        // 1. 현재 활성화 되어 있는 토큰 수 구하기
        long activeCnt     = queueTokenService.countActive();

        // 2. 활성화 가능한 토큰 수 반환
        long activeAbleCnt = request.maxToken()-activeCnt;

        // 3. 토큰 활성화
        if(activeAbleCnt>0){
            queueTokenService.activate(activeAbleCnt);
        }
    }


}
