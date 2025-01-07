package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import kr.hhplus.be.server.persentation.controller.queue_token.TokenApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    ApiResponse<TokenApiDto.GenerateTokenRes> create(TokenApiDto.GenerateTokenReq request){
        // 1. find user
        User user = userService.find(request.getUserId());

        // 2. Create Entity & Create
        QueueToken newQueueToken = queueTokenService.createToken(
                QueueToken.builder().user(user).build());

        return ApiResponse.ok(TokenApiDto.GenerateTokenRes.builder().tokenId(newQueueToken.getId()).build());
    }
}
