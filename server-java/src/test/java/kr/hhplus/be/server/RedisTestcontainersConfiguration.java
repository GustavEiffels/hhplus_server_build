package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Profile("test")
public class RedisTestcontainersConfiguration {
    private static final GenericContainer<?> REDIS_LOCK_CONTAINER;
    private static final GenericContainer<?> REDIS_QUEUE_CONTAINER;

    static {
        REDIS_LOCK_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.0.8-alpine"))
                .withExposedPorts(6379);
        REDIS_LOCK_CONTAINER.start();

        REDIS_QUEUE_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.0.8-alpine"))
                .withExposedPorts(6379);
        REDIS_QUEUE_CONTAINER.start();

        // 각각의 Redis 인스턴스에 대한 시스템 프로퍼티 설정
        System.setProperty("spring.redis-lock.host", REDIS_LOCK_CONTAINER.getHost());
        System.setProperty("spring.redis-lock.port", REDIS_LOCK_CONTAINER.getMappedPort(6379).toString());

        System.setProperty("spring.redis-queue.host", REDIS_QUEUE_CONTAINER.getHost());
        System.setProperty("spring.redis-queue.port", REDIS_QUEUE_CONTAINER.getMappedPort(6379).toString());
    }

    @PreDestroy
    public void preDestroy() {
        if (REDIS_LOCK_CONTAINER.isRunning()) {
            REDIS_LOCK_CONTAINER.stop();
        }
        if (REDIS_QUEUE_CONTAINER.isRunning()) {
            REDIS_QUEUE_CONTAINER.stop();
        }
    }
}
