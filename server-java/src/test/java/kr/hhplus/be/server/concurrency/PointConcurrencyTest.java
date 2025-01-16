package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    UserJpaRepository userJpaRepository;
}
