package kr.hhplus.be.server.infrastructure.queue_token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository{

    private final RedisTemplate<String,Object> redisTemplate;
    private final String ZSET_KEY = "WAITING_AREA";
    private final String HASH_KEY = "MAPPING_TABLE";
    private final String SET_KEY  = "ACTIVE_AREA";

    private ZSetOperations<String,Object> getZSET(){
        return redisTemplate.opsForZSet();
    }
    private HashOperations<String,String,Long> getHASH(){
        return redisTemplate.opsForHash();
    }
    private SetOperations<String,Object> getSET(){
        return redisTemplate.opsForSet();
    }


// HASH
    @Override
    public void insertMappingTable(String tokenId, Long userId) {
        getHASH().put(HASH_KEY,tokenId,userId);
    }

    @Override
    public Long findUserIdByTokenId(String tokenId) {
        Object value = getHASH().get(HASH_KEY, tokenId);

        if( value instanceof  Integer ){
            return ((Integer) value).longValue();
        }
        else{
            return (Long) value;
        }
    }

// WAITING_AREA
    @Override
    public void insertWaitingArea(String tokenId) {
        getZSET().add(ZSET_KEY,tokenId,System.currentTimeMillis());
    }

    @Override
    public Long findWaitingTokenByTokenId(String tokenId) {
        return getZSET().rank(ZSET_KEY,tokenId);
    }


    // ACTIVE_AREA
    @Override
    public boolean isActiveToken(String tokenId) {
        return getSET().isMember(SET_KEY,tokenId);
    }




    @Override
    public void insertActive(String tokenId) {
        getSET().add(SET_KEY,tokenId);
    }

    @Override
    public long countActive() {
        return getSET().size(SET_KEY);
    }

    @Override
    public void deleteActive(String tokenId) {
        getSET().remove(SET_KEY,tokenId);
    }
}
