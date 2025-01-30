package kr.hhplus.be.server.locking.redisson;

import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.PaymentFacadeDto;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.point_history.PointHistoryStatus;
import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.point_history.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            결제 - 분산락을 사용한다.      
            """)
@ActiveProfiles("redisson")
public class PaymentConcurrencyTest {

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
    PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    TokenJpaRepository tokenJpaRepository;

    @Autowired
    PaymentFacade facade;

    QueueToken queueToken;

    User user;

    List<Long> reservations;


    @DisplayName("""
            분산락을 사용하여 동시에 예약 결제를 10번 요청하면, 테스트 성공 시 
            결제는 1번만 성공하고,
            결제 2건, 
            결제 내역 2건 이 생성되고, 
            토큰은 만료가 되고 예약의 상태는 Reserved 가 된다.
            """)
    @Test
    void thread_10() throws InterruptedException {
        concurrencyTest(10);
    }


    @DisplayName("""
            분산락을 사용하여 동시에 예약 결제를 50번 요청하면, 테스트 성공 시
            결제는 1번만 성공하고, 
            결제 2건, 
            결제 내역 2건 이 생성되며, 
            토큰은 만료가 되고 예약의 상태는 Reserved 가 된다.
            """)
    @Test
    void thread_50() throws InterruptedException {
        concurrencyTest(50);
    }

    @DisplayName("""
            분산락을 사용하여 동시에 예약 결제를 200번 요청하면, 테스트 성공 시
            결제는 1번만 성공하고, 
            결제 2건, 
            결제 내역 2건 이 생성되며, 
            토큰은 만료가 되고 예약의 상태는 Reserved 가 된다.
            """)
    @Test
    void thread_200() throws InterruptedException {
        concurrencyTest(200);
    }

    @DisplayName("""
            분산락을 사용하여 동시에 예약 결제를 1000번 요청하면, 테스트 성공 시
            결제는 1번만 성공하고, 
            결제 2건, 
            결제 내역 2건 이 생성되며, 
            토큰은 만료가 되고 예약의 상태는 Reserved 가 된다.
            """)
    @Test
    void thread_1000() throws InterruptedException {
        concurrencyTest(1000);
    }

    void concurrencyTest(int numThreads) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        CountDownLatch doneSignal = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);


        List<Boolean> results = new ArrayList<>();
        HashSet<String> errorMessage = new HashSet<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {

                    PaymentFacadeDto.PaymentParam param = new PaymentFacadeDto.PaymentParam(
                            reservations, user.getId(), queueToken.getId());
                    try {
                        facade.pay(param);
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

        // 결제 성공은 1 번만
        long successfulPayments = results.stream().filter(Boolean::booleanValue).count();
        assertEquals(1,successfulPayments,"결제 성공은 1번만");

        // 사용자 포인트 검증
        User userAfterPayment = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(80_000L,userAfterPayment.getPoint(),"결제 후 사용자의 포인트는 80,000 포인트");

        // 모든 예약은 예약된 상태
        List<Reservation> reservationList = reservationRepository.findAllById(reservations);
        reservationList.forEach(item-> assertEquals(ReservationStatus.RESERVED,item.getStatus()));

        // 모든 결제 내역은 status USE
        List<PointHistory> pointHistories = pointHistoryJpaRepository.findAll();
        pointHistories.forEach(item->{
            assertEquals(PointHistoryStatus.USE,item.getStatus());
            assertEquals(10_000L,item.getAmount());
        });

        // 토큰은 만료 되어야 한다.
        QueueToken findToken = tokenJpaRepository.findById(queueToken.getId()).orElseThrow();
        assertTrue(findToken.getExpireAt().isBefore(LocalDateTime.now()));

        log.info("결제 성공 횟수 : {}",successfulPayments);
        log.info("생성된 결제 수 : {}",reservationList.size());
        log.info("생성된 결제 내역 수 : {}",pointHistories.size());
        log.info("결제 후 사용자 금액 : {}",userAfterPayment.getPoint());
        errorMessage.forEach(item->log.info("발생한 예외 : {}",item));
        log.info("process time  : {} ms",(endTime-startTime));

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        concertRepository.deleteAll();
        concertScheduleRepository.deleteAll();
        seatRepository.deleteAll();
        tokenJpaRepository.deleteAll();
        reservationRepository.deleteAll();
    }


    @BeforeEach
    void setUp(){

        // 사용자 생성
        User newUser = User.create("김연습");
        newUser.pointTransaction(100_000L);
        newUser = userRepository.save(newUser);

        // 토큰 생성
        QueueToken newToken = QueueToken.create(newUser);
        newToken = tokenJpaRepository.save(newToken);

        // 콘서트 & 콘서트 스케줄 생성
        Concert newConcert = Concert.create("갈라쇼","이갈라");
        concertRepository.save(newConcert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .showTime(LocalDateTime.now().plusHours(3))
                .reserveEndTime(LocalDateTime.now().plusHours(2))
                .reserveStartTime(LocalDateTime.now().plusHours(1))
                .concert(newConcert)
                .build();
        concertScheduleRepository.save(concertSchedule);

        // 좌석 생성
        Seat seat1 = Seat.builder().seatNo(1).concertSchedule(concertSchedule).price(10_000L).build();
        Seat seat2 = Seat.builder().seatNo(2).concertSchedule(concertSchedule).price(10_000L).build();
        seatRepository.save(seat1);
        seatRepository.save(seat2);

        // 예약 생성
        Reservation reservation1 = Reservation.create(newUser,seat1);
        Reservation reservation2 = Reservation.create(newUser,seat2);
        reservation1 = reservationRepository.save(reservation1);
        reservation2 = reservationRepository.save(reservation2);


        List<Long> newReservationList = new ArrayList<>();
        newReservationList.add(reservation1.getId());
        newReservationList.add(reservation2.getId());

        user = newUser;
        queueToken = newToken;
        reservations = newReservationList;
    }
}
