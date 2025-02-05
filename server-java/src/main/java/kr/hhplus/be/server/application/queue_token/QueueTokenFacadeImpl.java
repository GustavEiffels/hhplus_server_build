package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenServiceImpl;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class QueueTokenFacadeImpl implements QueueTokenFacade{

    private final UserService userService;
    private final QueueTokenServiceImpl queueTokenService;


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
        QueueToken newQueueToken = queueTokenService.createToken( QueueToken.create(user) );

        return new QueueTokenFacadeDto.CreateResult(newQueueToken.getId().toString());
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
        return new QueueTokenFacadeDto.ActiveCheckResult( queueTokenService.isValidAndActive(param.tokenId(), param.userId()) );
    }


    /**
     * 최대 활성화 가능 토큰 수까지 토큰 활성화
     * @param param
     */
    @Transactional
    public void activate(QueueTokenFacadeDto.ActivateParam param){
        queueTokenService.activate(param.maxTokenCnt());
    }


}
