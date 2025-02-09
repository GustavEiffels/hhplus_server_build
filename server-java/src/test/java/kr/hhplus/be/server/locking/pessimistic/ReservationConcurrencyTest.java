package kr.hhplus.be.server.locking.pessimistic;


import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            좌석 예약 - 비관적 락을 사용한다.      
            """)
//@ActiveProfiles("pessimistic")
public class ReservationConcurrencyTest {

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

    ConcertSchedule concertSchedule;
    Seat seat;
    List<User> userList;

    @DisplayName("비관적 락을 사용하여 하나의 좌석을 10 개의 요청이 동시에 요청할 때, 해당 좌석에 대한 예약은 1번만 수행 된다.")
    @Test
    void thread_10() throws InterruptedException {
        concurrencyTest(10);
    }

    @DisplayName("비관적 락을 사용하여 하나의 좌석을 50 개의 요청이 동시에 요청할 때, 해당 좌석에 대한 예약은 1번만 수행 된다.")
    @Test
    void thread_50() throws InterruptedException {
        concurrencyTest(50);
    }

    @DisplayName("비관적 락을 사용하여 하나의 좌석을 200 개의 요청이 동시에 요청할 때, 해당 좌석에 대한 예약은 1번만 수행 된다.")
    @Test
    void thread_200() throws InterruptedException {
        concurrencyTest(200);
    }

    @DisplayName("비관적 락을 사용하여 하나의 좌석을 1000 개의 요청이 동시에 요청할 때, 해당 좌석에 대한 예약은 1번만 수행 된다.")
    @Test
    void thread_1000() throws InterruptedException {
        concurrencyTest(1000);
    }

    void concurrencyTest(int threadCnt) throws InterruptedException {
        createUser(threadCnt);

        long startTime = System.currentTimeMillis();
        CountDownLatch doneSignal = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        List<Boolean>  results = new ArrayList<>(threadCnt);
        HashSet<String> errorMessage = new HashSet<>(threadCnt);

        for (User user : userList) {
            executorService.submit(() -> {
                try {
                    List<Long> seatIds = new ArrayList<>();
                    seatIds.add(seat.getId());

                    ReservationFacadeDto.ReservationParam reservationParam =
                            new ReservationFacadeDto.ReservationParam(concertSchedule.getId(), seatIds, user.getId());
                    try {
                        reservationFacade.reservation(reservationParam);
                        results.add(true);

                    } catch (Exception e) {
                        errorMessage.add(e.getClass().getName()+" : "+e.getLocalizedMessage());
                        results.add(false);
                    }
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
        executorService.shutdown();
        long endTime = System.currentTimeMillis();

        long successfulReservations = results.stream().filter(Boolean::booleanValue).count();
        long failedReservations = results.stream().filter(result -> !result).count();
        int  newReservationCnt  = reservationRepository.findAll().size();

        Assertions.assertEquals(1,successfulReservations,"성공한 예약 개수 : "+successfulReservations);
        Assertions.assertEquals(successfulReservations,newReservationCnt,"생성된 예약 수");
        errorMessage.forEach(item->log.info("예외 : {}",item));

        log.info("성공한 예약 개수 : {}",successfulReservations);
        log.info("실패한 예외 개수 : {}",failedReservations);
        log.info("생성된 예약 수   : {}",newReservationCnt);
        log.info("process time  : {} ms",(endTime-startTime));
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
