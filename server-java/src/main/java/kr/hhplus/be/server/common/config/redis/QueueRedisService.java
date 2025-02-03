package kr.hhplus.be.server.common.config.redis;

import jdk.jfr.Frequency;
import org.redisson.api.RBucket;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class QueueRedisService {
    private final RedissonClient redissonClient;

    public QueueRedisService(@Qualifier("queueRedissonClient") RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    // SET
    public void addString(String key, String value){
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    // GET
    public String popString(String key){
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    // DEL
    public void delString(String key){
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }



}
