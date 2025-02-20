package kr.hhplus.be.server.kafka;


import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import kr.hhplus.be.server.domain.outbox.OutBoxStatus;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.outbox.OutBoxJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.consumer.ReservationCreateConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
        Kafka - Reservation 통합 테스트 
        """)
public class ReserveIntegrationTest {

    @Autowired
    ReservationFacade reservationFacade;
    @Autowired
    ConcertJpaRepository concertRepository;
    @Autowired
    ScheduleJpaRepository concertScheduleRepository;
    @Autowired
    UserJpaRepository userRepository;
    @Autowired
    SeatJpaRepository seatRepository;
    @Autowired
    ReservationJpaRepository reservationRepository;

    @Autowired
    OutBoxJpaRepository outBoxJpaRepository;

    @Autowired
    OutBoxService outBoxService;

    @Autowired
    ReservationCreateConsumer reservationCreateConsumer;



    ConcertSchedule concertSchedule;
    Seat seat;
    List<User> userList;


    @DisplayName("""
            예약이 완료되어 Kafka 로 이벤트를 발생 시키면
            DataPlatform 에서 예약 정보를 사용하는 로직에서,
            OutBox 의 상태를 PROCESSED 로 변경한다.  
            """)
    @Test
    void reservationKafkaEventTest(){
        reservationCallOutKafka();
    }

    @DisplayName("""
            데이터 플랫폼에서 사용이 완료된 이벤트와 유효한 이벤트 1개, 
            만료되거나 실패한 이벤트가 2개가 추가 된 후
            만료된 이벤트 스케줄의 핵심 로직이 작동하면, 
            총 4개의 이벤트들 중 3개가 삭제되고 1개의 이벤트 만이 존재한다. 
            """)
    @Test
    void reservationEventDeleteTest(){
        // given
        reservationCallOutKafka();
        OutBox outBox1 = OutBox.create("test1","test1");
        outBox1.failed();
        OutBox outBox2 = OutBox.create("test2","test2");
        outBox2.processed();
        OutBox outBox3 = OutBox.create("test3","test3");
        outBoxService.create(outBox1);
        outBoxService.create(outBox2);
        outBoxService.create(outBox3);

        // when
        outBoxService.deleteExpired();

        // then
        List<OutBox> outBoxList = outBoxJpaRepository.findAll();
        Assertions.assertEquals(1,outBoxList.size());
        Assertions.assertEquals(outBox3.getPayload(),outBoxList.get(0).getPayload());
    }

    void reservationCallOutKafka(){
        // given
        // create user
        createUser(1);
        User user = userList.get(0);

        // create seat
        List<Long> seatIds = new ArrayList<>();
        seatIds.add(seat.getId());

        ReservationFacadeDto.ReservationParam param =
                new ReservationFacadeDto.ReservationParam(concertSchedule.getId(), seatIds, user.getId());
        reservationFacade.reservation(param);

        await().pollDelay(2, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(()->{
                    // when & then
                    reservationCreateConsumer.awaitLatch();
                    outBoxJpaRepository.findAll().forEach(
                            item ->Assertions.assertEquals(OutBoxStatus.PROCESSED,item.getStatus(),"OutBox 의 상태는 PROCESSED"));
                });
    }


    @BeforeEach
    void setUp(){
        // Create Concert
        Concert concert = Concert.create("광대쇼","김광대");
        concertRepository.save(concert);

        // Create Concert Schedule
        ConcertSchedule newConcertSchedule = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(LocalDateTime.now().plusHours(1))
                .reserveEndTime(LocalDateTime.now().plusHours(2))
                .showTime(LocalDateTime.now().plusHours(3))
                .build();
        newConcertSchedule = concertScheduleRepository.save(newConcertSchedule);

        // Create Seats
        seat = seatRepository.save(
                Seat.builder()
                        .seatNo(1)
                        .price(10_000L)
                        .concertSchedule(newConcertSchedule)
                        .build());

        concertSchedule = newConcertSchedule;
    }

    @AfterEach
    void tearDown() {
        outBoxJpaRepository.deleteAll();
        userRepository.deleteAll();
        concertRepository.deleteAll();
        concertScheduleRepository.deleteAll();
        seatRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    void createUser(int threadCnt){
        List<User> newUserList = new ArrayList<>();
        for(int i = 1; i<=threadCnt; i++){
            User newUser = User.create("김연습"+i);
            newUser.pointTransaction(100_000L);
            newUserList.add(newUser);
        }
        userList = userRepository.saveAll(newUserList);
    }


}
