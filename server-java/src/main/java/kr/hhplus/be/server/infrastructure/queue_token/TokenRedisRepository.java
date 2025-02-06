package kr.hhplus.be.server.infrastructure.queue_token;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface TokenRedisRepository {

// MAPPING_TABLE :
    void putMappingTable(String tokenId,Long userId);

    Long findUserIdFromMappingTable(String tokenId);

    void deleteFromMappingTable(String tokenId);

// WAITING_AREA
    void putWaiting(String tokenId);

    Long getRankFromWaiting(String tokenId);

    Set<ZSetOperations.TypedTuple<Object>> popFromWaiting(long activateCnt);

    long countWaiting();


// ACTIVE_AREA
    void putActive(String tokenId);

    Double getScoreFromActive(String tokenId);

    Set<Object> findExpiredFromActive(Long expireTime);
    void deleteByScoreFromActive(Long expireTime);

    long countActive();

    void deleteFromActive(String tokenId);

}
