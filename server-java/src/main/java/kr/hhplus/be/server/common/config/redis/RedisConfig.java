package kr.hhplus.be.server.common.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis-lock.host}")
    private String lockHost;

    @Value("${spring.redis-lock.port}")
    private int lockPort;

    @Value("${spring.redis-queue.host}")
    private String queueHost;

    @Value("${spring.redis-queue.port}")
    private int queuePort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redissonClient = null;
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + lockHost + ":" + lockPort);
        redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
