package kr.hhplus.be.server.infrastructure.queue_token;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository{

    @Value("${queue.active-token-expire}")
    private long expireSecond;

    private final RedisTemplate<String,Object> redisTemplate;
    private final String MAPPING_HASH = "MAPPING_TABLE";
    private final String WAITING_SET  = "WAITING_AREA";
    private final String ACTIVE_SET   = "ACTIVE_AREA";

    private ZSetOperations<String,Object> getZSET(){
        return redisTemplate.opsForZSet();
    }
    private HashOperations<String,String,Long> getHASH(){
        return redisTemplate.opsForHash();
    }


// HASH-MAPPING TABLE
    @Override
    public void putMappingTable(String tokenId, Long userId) {
        getHASH().put(MAPPING_HASH,tokenId,userId);
    }

    @Override
    public Long findUserIdFromMappingTable(String tokenId) {
        Object value = getHASH().get(MAPPING_HASH, tokenId);
        if( value instanceof  Integer ){
            return ((Integer) value).longValue();
        }
        else{
            return (Long) value;
        }
    }

    @Override
    public void deleteFromMappingTable(String tokenId) {
        getHASH().delete(MAPPING_HASH,tokenId);
    }

// ZSET-WAITING AREA
    @Override
    public void putWaiting(String tokenId) {
        getZSET().add(WAITING_SET,tokenId,System.currentTimeMillis());
    }

    @Override
    public Long getRankFromWaiting(String tokenId) {
        return getZSET().rank(WAITING_SET,tokenId);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> popFromWaiting(long activateCnt) {
        // popMin 메서드를 호출하여 데이터를 꺼냄
        Set<ZSetOperations.TypedTuple<Object>> poppedValues = getZSET().popMin(WAITING_SET, activateCnt);

        // 데이터를 처리할 로직을 추가하거나, 그냥 반환
        return poppedValues != null ? poppedValues : Collections.emptySet();
    }

    @Override
    public long countWaiting() {
        return getZSET().zCard(WAITING_SET);
    }

// ACTIVE_AREA
    @Override
    public void putActive(String tokenId) {
        getZSET().add(ACTIVE_SET,tokenId,System.currentTimeMillis()+(expireSecond*1000));
    }

    @Override
    public Double getScoreFromActive(String tokenId) {
        return getZSET().score(ACTIVE_SET,tokenId);
    }

    @Override
    public Set<Object> findExpiredFromActive(Long expireTime) {
        return getZSET().rangeByScore(ACTIVE_SET,0,expireTime);
    }

    @Override
    public void deleteByScoreFromActive(Long expireTime) {
        getZSET().removeRangeByScore(ACTIVE_SET,0,expireTime);
    }

    @Override
    public long countActive() {
        return getZSET().zCard(ACTIVE_SET);
    }

    @Override
    public void deleteFromActive(String tokenId) {
        getZSET().remove(ACTIVE_SET,tokenId);
    }


}
