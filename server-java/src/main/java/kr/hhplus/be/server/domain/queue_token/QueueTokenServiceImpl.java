package kr.hhplus.be.server.domain.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueTokenServiceImpl implements QueueTokenService{
    private final QueueTokenRepository repository;
    @Value("${queue.max-active-token}")
    private long maxActiveToken;

    /**
     * 토큰 활성화 시키기
     * redis
     */
    public void activate(Long tokenCnt){

        if(tokenCnt != null && tokenCnt > 30){
            maxActiveToken = tokenCnt;
        }

        // 1. 현재 활성화 되어 있는 토큰 수 구하기
        long currentActiveCnt     = repository.countActive();

        // 2. 활성화 가능한 토큰 수 반환
        long activeAbleCnt        = maxActiveToken-currentActiveCnt;


        // 3. 대기열에서 제외
        Set<String> tokenSet = repository.popFromWaiting(activeAbleCnt).stream()
                .map(tuple -> String.valueOf(tuple.getValue()))
                .collect(Collectors.toSet());

        // 4. 제외한 토큰 활성화
        tokenSet.forEach(repository::putActive);
    }
// REDIS

    /**
     * REDIS 를 사용하여
     * 대기열 토큰 생성 및
     * 대기열에 토큰 추가
      * @param userId
     * @return
     */
    @Override
    public String createToken(Long userId) {
        // 1.Create UUID
        String tokenId = UUID.randomUUID().toString();

        // 2. Insert into Redis Hash : Mapping Table
        repository.putMappingTable(tokenId,userId);

        // 3. Insert into ZSET : WAITING AREA ( WAITING QUEUE )
        repository.putWaiting(tokenId);

        return tokenId;
    }

    @Override
    public Boolean isValidAndActive(Long userId,String tokenId) {
        // 1. Find UserId By TokenId  ( Redis )
        Long findUserId = repository.findUserIdFromMappingTable(tokenId);

        // 2. NOT FOUND USER BY TOKEN ID
        if( findUserId == null ){
            throw new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN);
        }

        // 3. NOT EQUAL TOKEN OWNER WITH USER
        if(!findUserId.equals(userId)){
            throw new BusinessException(ErrorCode.NOT_MATCHED_WITH_USER);
        }

        Long   waitingRank           = repository.getRankFromWaiting(tokenId);
        Double activeTokenExpireTime = repository.getScoreFromActive(tokenId);


        if(waitingRank!=null){
            return  false;
        }
        if(activeTokenExpireTime > System.currentTimeMillis()){
            return true;
        }
        else if(activeTokenExpireTime <= System.currentTimeMillis()){
            throw new BusinessException(ErrorCode.EXPIRE_QUEUE_TOKEN);
        }
        else{
            throw new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN);
        }
    }


    @Override
    public void expired(String tokenId) {
        repository.deleteFromActive(tokenId);
    }

    @Override
    public void expireToken() {
        // 1. 현재 시간 측정
        Long currentTime = System.currentTimeMillis();

        // 2. 삭제해야할 목록 가져오기
        Set<String> deleteTokenSet = repository.findExpiredFromActive(currentTime).stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toSet());

        // 3. active 토큰 삭제
        repository.deleteByScoreFromActive(currentTime);


        // 4. Mapping Table 에서 토큰을 제거 :: 토큰과 유저간의 관계 제거
        deleteTokenSet.forEach(repository::deleteFromMappingTable);
    }


}
