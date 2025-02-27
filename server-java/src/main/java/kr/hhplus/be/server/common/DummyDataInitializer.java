package kr.hhplus.be.server.common;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements CommandLineRunner {
    private final UserJpaRepository userJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final ScheduleJpaRepository concertScheduleRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void run(String... args) throws Exception {
        int USER_CNT  = 1_000;

        // redis 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // USER 추가
        createUser(USER_CNT);

        // Concert 생성
        Concert concert = createConcert();

        // ConcertSchedule And Seat 생성
        createConcertScheduleAndSeat(USER_CNT,concert);
    }

    private void createUser(int userCnt){
        for (int i = 0; i < userCnt; i++) {
            User user = userJpaRepository.save(User.create("test" + i));
            userJpaRepository.save(userService.chargePoints(user.getId(), 10000L));
        }
    }

    private Concert createConcert(){
        return concertJpaRepository.save(Concert.create("ConcertTitle", "ConcertPlayer"));
    }

    private void createConcertScheduleAndSeat(int userCnt, Concert concert){

        for (int i = 0; i < userCnt; i++) {
            ConcertSchedule concertSchedule = concertScheduleRepository.save(ConcertSchedule.builder()
                    .concert(concert)
                    .reserveStartTime(LocalDateTime.now().minusDays(1))
                    .reserveEndTime(LocalDateTime.now().plusMonths(1))
                    .showTime(LocalDateTime.now().plusMonths(2))
                    .build());

            seatJpaRepository.save(Seat.builder()
                    .price(10000L)
                    .seatNo(1)
                    .concertSchedule(concertSchedule)
                    .build());
        }
    }
}

