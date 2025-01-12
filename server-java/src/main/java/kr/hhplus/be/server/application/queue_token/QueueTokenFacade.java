package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
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
     * @param param
     * @return
     */
    @Transactional
    public QueueTokenFacadeDto.CreateResult create(QueueTokenFacadeDto.CreateParam param){
        // 1. find user
        User user = userService.findUser(param.userId());

        // 2. Create Entity & Create
        QueueToken newQueueToken = queueTokenService.createToken(
                QueueToken.builder().user(user).build());

        return new QueueTokenFacadeDto.CreateResult(newQueueToken.getId());
    }


    /**
     * 인터셉터에서 토큰이 활성화가 되었는지 확인
     * @param param
     * @return
     */
    public QueueTokenFacadeDto.ActiveCheckResult isValidToken(QueueTokenFacadeDto.ActiveCheckParam param){
        // 1. 사용자 조회
        userService.findUser(param.userId());

        // 2. 활성화된 토큰인지 확인
        return new QueueTokenFacadeDto.ActiveCheckResult(queueTokenService.isValidAndActive(param.tokenId(), param.userId()));
    }


    /**
     * 최대 활성화 가능 토큰 수까지 토큰 활성화
     * @param param
     */
    @Transactional
    public void activate(QueueTokenFacadeDto.ActivateParam param){
        // 1. 현재 활성화 되어 있는 토큰 수 구하기
        long activeCnt     = queueTokenService.countActive();

        // 2. 활성화 가능한 토큰 수 반환
        long activeAbleCnt = param.maxTokenCnt()-activeCnt;

        // 3. 토큰 활성화
        if(activeAbleCnt>0){
            queueTokenService.activate(activeAbleCnt);
        }
    }


}
