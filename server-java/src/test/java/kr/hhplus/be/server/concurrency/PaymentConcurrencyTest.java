package kr.hhplus.be.server.concurrency;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
@ActiveProfiles("redisson")
@Slf4j
@DisplayName("결제 통합 테스트")
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
                 * 한명의 사용자가
                 * 하나의 예약에 대해서
                 * 결제를 하려고 한다.
                 * 이때 결제가 여러번 발생해도
                 * 최종적으로 결제는 한번만 일어나야한다.
                 *
                 * 사용자는 보유 포인트가 100,000 포인트
                 * 10,000 짜리 2좌석 예약을 결제하려고 한다.
                 *
                 * 서버의 실수로 인해서
                 * 한번에 30번 요청이 갔다고 할때
                 *
                 * 사용자의 포인트는 80,000 포인트가 되어야하고
                 * 2개의 Reservation 의 상태값은 RESERVE 로 변경되어야 한다.
                 * Token 은 만료 되어 있어한다.            
            """)
    @Test
    void concurrencyTest() throws InterruptedException {
        int numThreads = 30; // 동시 요청의 개수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(numThreads);

        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    startSignal.await();

                    PaymentFacadeDto.PaymentParam param = new PaymentFacadeDto.PaymentParam(
                            reservations, user.getId(), queueToken.getId().toString());
                    try {
                        facade.pay(param); // 결제 시도
                        results.add(true); // 결제 성공
                    } catch (Exception e) {
                        results.add(false); // 결제 실패
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown(); // 작업 완료 신호
                }
            });
        }

        startSignal.countDown();
        doneSignal.await();
        executorService.shutdown();

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
