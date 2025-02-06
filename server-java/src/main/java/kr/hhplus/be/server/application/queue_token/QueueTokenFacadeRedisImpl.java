package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.queue_token.QueueTokenServiceImpl;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("redis")
public class QueueTokenFacadeRedisImpl implements QueueTokenFacade{

    private final UserService userService;
    private final QueueTokenService queueTokenService;


    /**
     * USECASE : 1. 유저 대기열 토큰 발급 받는 API
     * REDIS 변환 완료
     * @param param
     * @return
     */

    public QueueTokenFacadeDto.CreateResult create(QueueTokenFacadeDto.CreateParam param){
        // 1. find user
        User user = userService.findUser(param.userId());

        // 2. Create Token and Insert into Waiting Area
        String tokenId = queueTokenService.createToken(user.getId());

        return new QueueTokenFacadeDto.CreateResult(tokenId);
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
        return new QueueTokenFacadeDto.ActiveCheckResult( queueTokenService.isValidAndActive(param.userId(),param.tokenId()) );
    }


    /**
     * 최대 활성화 가능 토큰 수까지 토큰 활성화
     */
    public void activate(){
        queueTokenService.activate();
    }

    @Override
    public void expire() {
        queueTokenService.expireToken();
    }


}
