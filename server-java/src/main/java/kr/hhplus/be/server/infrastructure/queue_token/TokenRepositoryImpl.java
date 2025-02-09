package kr.hhplus.be.server.infrastructure.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Profile("redis")
public class TokenRepositoryImpl implements QueueTokenRepository {
    private final TokenJpaRepository tokenJpaRepository;

    private final TokenRedisRepository tokenRedisRepository;


    @Override
    public List<QueueToken> findTokensToActivate(long activateCnt) {
        return tokenJpaRepository.findTokensToActivate(activateCnt);
    }

    // ACTIVE 토큰 개수 구하기
    @Override
    public long countActiveTokens() {
        return tokenRedisRepository.countActive();
    }


    // 토큰 생성
    @Override
    public QueueToken create(QueueToken queueToken){
        return tokenJpaRepository.save(queueToken);
    }


    /**
     * 토큰아이디로
     * 토큰과 유저 정보를 페치 조인하여 함께 가져오는 함수
     * @param queueTokenId
     * @return
     */
    @Override
    public Optional<QueueToken> findByIdWithUser(Long queueTokenId){
        return tokenJpaRepository.findByIdWithUser(queueTokenId);
    }

    @Override
    public Optional<QueueToken> findById(Long tokenId) {
        return tokenJpaRepository.findById(tokenId);
    }


// MAPPING TABLE
    @Override
    public void putMappingTable(String tokenId, Long userId) {
        tokenRedisRepository.putMappingTable(tokenId,userId);
    }

    @Override
    public Long findUserIdFromMappingTable(String tokenId) {
        return tokenRedisRepository.findUserIdFromMappingTable(tokenId);
    }

    @Override
    public void deleteFromMappingTable(String tokenId) {
        tokenRedisRepository.deleteFromMappingTable(tokenId);
    }

// WAITING AREA
    @Override
    public void putWaiting(String tokenId) {
        tokenRedisRepository.putWaiting(tokenId);
    }

    @Override
    public Long getRankFromWaiting(String tokenId) {
        return tokenRedisRepository.getRankFromWaiting(tokenId);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> popFromWaiting(long activateCnt) {
        return tokenRedisRepository.popFromWaiting(activateCnt);
    }

    @Override
    public long countWaiting() {
        return tokenRedisRepository.countWaiting();
    }

// ACTIVE AREA
    @Override
    public void putActive(String tokenId) {
        tokenRedisRepository.putActive(tokenId);
    }
    @Override
    public Double getScoreFromActive(String tokenId) {
        return tokenRedisRepository.getScoreFromActive(tokenId);
    }

    @Override
    public Set<Object> findExpiredFromActive(Long expireTime) {
        return tokenRedisRepository.findExpiredFromActive(expireTime);
    }

    @Override
    public void deleteByScoreFromActive(Long expireTime) {
        tokenRedisRepository.deleteByScoreFromActive(expireTime);
    }

    @Override
    public long countActive() {
        return tokenRedisRepository.countActive();
    }

    @Override
    public void deleteFromActive(String tokenId) {
        tokenRedisRepository.deleteFromActive(tokenId);
    }



}
