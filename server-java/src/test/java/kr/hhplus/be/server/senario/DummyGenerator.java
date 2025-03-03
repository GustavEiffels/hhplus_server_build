package kr.hhplus.be.server.senario;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;


@SpringBootTest
@ActiveProfiles("local")
public class DummyGenerator {

    @Autowired
    UserJpaRepository    userJpaRepository;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ScheduleJpaRepository concertScheduleRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;


    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;


    @DisplayName("""
            성능 테스트를 하기 위해서 1000 명의 사용자들을 생성한다.   
            """)
    @Test
    void createDemo() {

        // create 1000 user
        for (int i = 0; i < 1000; i++) {
            User user = userJpaRepository.save(User.create("test" + i));
            userJpaRepository.save(userService.chargePoints(user.getId(), 10000L));
        }

        // Add 1 Concert
        Concert newConcert = concertJpaRepository.save(Concert.create("ConcertTitle", "ConcertPlayer"));



        // Add 9999 Concert Schedule
        for (int i = 0; i < 10000; i++) {
            ConcertSchedule concertSchedule = concertScheduleRepository.save(ConcertSchedule.builder()
                    .concert(newConcert)
                    .reserveStartTime(LocalDateTime.now().minusDays(1))
                    .reserveEndTime(LocalDateTime.now().plusMonths(1))
                    .showTime(LocalDateTime.now().plusMonths(2))
                    .build());

            seatJpaRepository.save(Seat.builder()
                    .price(10000L)
                    .seatNo(i%50+1)
                    .concertSchedule(concertSchedule)
                    .build());
        }
    }

    @Test
    void redisFlush () {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
