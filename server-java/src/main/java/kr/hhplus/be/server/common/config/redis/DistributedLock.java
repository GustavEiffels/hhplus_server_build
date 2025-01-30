package kr.hhplus.be.server.common.config.redis;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String lockNm();

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    long waitTime() default 5L;

    long leaseTime() default 100L;
}
