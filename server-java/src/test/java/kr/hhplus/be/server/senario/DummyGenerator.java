package kr.hhplus.be.server.senario;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
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
    UserService userService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;


    @DisplayName("""
            성능 테스트를 하기 위해서 1000 명의 사용자들을 생성한다.   
            """)
//    @Test
    void createUser(){
        // delete all user
        deleteAllUser();

        // create 1000 user
        for(int i = 0; i<1000;i++){
            User user = userJpaRepository.save(User.create("test"+i));
            userJpaRepository.save(userService.chargePoints(user.getId(),10000L));
        }
    }

    @DisplayName("""
            성능 테스트를 하기 위해서 1개의 콘서트와 1000개의,
            콘서트 스케줄을 생성 한다.
            """)
//    @Test
    void createConcertAndConcertSchedule(){
        // delete concertSchedule
        deleteAllConcertSchedule();
        // delete concert
        deleteAllConcert();

        // Add 1 Concert
        Concert newConcert = concertJpaRepository.save(Concert.create("ConcertTitle","ConcertPlayer"));

        // Add 10000 Concert Schedule
        for(int i = 0; i<10000; i++){
            concertScheduleRepository.save(ConcertSchedule.builder()
                            .concert(newConcert)
                            .reserveStartTime(LocalDateTime.now().minusDays(1))
                            .reserveEndTime(LocalDateTime.now().plusMonths(1))
                            .showTime(LocalDateTime.now().plusMonths(2))
                            .build());
        }
    }





    void deleteAllUser(){
        userJpaRepository.deleteAll();
    }
    void deleteAllConcert(){
        concertJpaRepository.deleteAll();
    }

    void deleteAllConcertSchedule(){
        concertScheduleRepository.deleteAll();
    }


    @Test
    void redisFlush(){
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

}
