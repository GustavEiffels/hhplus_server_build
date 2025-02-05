package kr.hhplus.be.server.domain.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Profile("local")
public class QueueTokenServiceImpl implements QueueTokenService{
    private final QueueTokenRepository repository;

    /**
     * 토큰 활성화 시키기
     * @param maxTokenCnt
     */
    public void activate(Long maxTokenCnt){
        // 1. 현재 활성화 되어 있는 토큰 수 구하기
        long currentActiveCnt     = repository.countActiveTokens();

        // 2. 활성화 가능한 토큰 수 반환
        long activeAbleCnt        = maxTokenCnt-currentActiveCnt;

        // 3. 토큰 활성화
        if(activeAbleCnt>0){
            repository.findTokensToActivate(activeAbleCnt)
                    .forEach(QueueToken::activate);
        }
    }

    /**
     * 토큰 만료 시키기
     * @param tokenId
     */
    public void expired(Long tokenId){
        QueueToken token = repository.findById(tokenId)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN));
        token.expire();
    }


    /**
     * 대기열 토큰 생성
     * @param queueToken
     * @return
     */
    public QueueToken createToken(QueueToken queueToken){
        return repository.create(queueToken);
    }


    /**
     * 대기열 토큰이 유효하며, active 된 토큰인지 확인
     * @param queueTokenId
     * @param userId
     * @return
     */
    public Boolean isValidAndActive(Long queueTokenId, Long userId){

        QueueToken queueToken = repository.findByIdWithUser(queueTokenId)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN));

        if(!Objects.equals(queueToken.getUser().getId(), userId)){
            throw new BusinessException(ErrorCode.NOT_MATCHED_WITH_USER);
        }

        if( queueToken.getStatus().equals(QueueTokenStatus.WAIT) ){
            return false;
        }

        if( queueToken.getExpireAt().isBefore(LocalDateTime.now()) ){
            throw new BusinessException(ErrorCode.EXPIRE_QUEUE_TOKEN);
        }

        return true;
    }

    @Override
    public String createToken(Long userId) {
        return null;
    }

    @Override
    public Boolean isValidAndActive(Long userId, String tokenId) {
        return null;
    }


}
