package kr.hhplus.be.server.domain.queue_token;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QueueTokenRepository {

    List<QueueToken> findTokensToActivate(long activateCnt);

    /**
     * ACTIVE 인 토큰 개수 반환
     * - redis
     * @return
     */
    long countActiveTokens();

    /**
     * 토큰 생성
     * @param queueToken
     * @return
     */
    QueueToken create(QueueToken queueToken);

    /**
     * 토큰 조회
     * @param queueTokenId
     * @return
     */
    Optional<QueueToken> findByIdWithUser(Long queueTokenId);


    Optional<QueueToken> findById(Long tokenId);


// MAPPING
    void putMappingTable(String tokenId, Long userId);

    Long findUserIdFromMappingTable(String tokenId);

    void deleteFromMappingTable(String tokenId);

// WAITING
    void putWaiting(String tokenId);

    Long getRankFromWaiting(String tokenId);

    Set<ZSetOperations.TypedTuple<Object>> popFromWaiting(long activateCnt);

    long countWaiting();

// ACTIVE
    void putActive(String tokenId);
    Double getScoreFromActive(String tokenId);
    Set<Object> findExpiredFromActive(Long expireTime);

    void deleteByScoreFromActive(Long expireTime);

    long countActive();

    void deleteFromActive(String tokenId);




}
