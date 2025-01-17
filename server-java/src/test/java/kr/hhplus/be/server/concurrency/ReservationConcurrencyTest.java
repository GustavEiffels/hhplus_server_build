package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@SpringBootTest
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

    List<User> userList;
    Seat seat;

    ConcertSchedule concertSchedule;

    /**
     * 10명의 사용자가 동시에 한 좌석에 대해서 예약을 생성하려고 시도 합니다.
     * 이때, 단 한명의 유저만 예약이 되어야 하고
     * 예약은 단 한건만 생성이 되어야합니다.
     */
    @DisplayName("""
                 10명의 사용자가 동시에 한 좌석에 대해서 예약을 생성하려고 시도 합니다.
                 이때, 단 한명의 유저만 예약이 되어야 하고
                 예약은 단 한건만 생성이 되어야합니다.
            """)
    @Test
    void concurrencyTest() throws InterruptedException {

        int numThreads = 10;
        CountDownLatch doneSignal = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Boolean> results = new ArrayList<>(numThreads);
        List<BusinessException> businessExceptions = new ArrayList<>(numThreads);

        // 10명의 사용자에 대해 예약을 시도하는 스레드 생성
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

                    } catch (BusinessException e) {
                        businessExceptions.add(e);
                        results.add(false);
                    }
                } finally {
                    doneSignal.countDown(); // 작업이 끝날 때마다 countDown 호출
                }
            });
        }

        doneSignal.await();

        long successfulReservations = results.stream().filter(Boolean::booleanValue).count();
        long failedReservations = results.stream().filter(result -> !result).count();


        Assertions.assertEquals(1,successfulReservations,"성공한 예약 개수 : "+successfulReservations);
        Assertions.assertEquals(9,failedReservations,"성공한 예약 개수 : "+failedReservations);
        Assertions.assertEquals(1,reservationRepository.findAll().size(),"생성된 예약 수");
        businessExceptions.forEach(item->{
            Assertions.assertEquals(ErrorCode.NOT_RESERVABLE_DETECTED,item.getErrorStatus());
            Assertions.assertEquals(ErrorCode.NOT_RESERVABLE_DETECTED.getMessage(),item.getMessage());
        });




    }

    @BeforeEach
    void setUp(){

        // Create User
        List<User> newUserList = new ArrayList<>();
        for(int i = 1; i<=10; i++){
            User newUser = User.create("김연습"+i);
            newUser.pointTransaction(100_000L);
            newUserList.add(newUser);
        }
        newUserList = userRepository.saveAll(newUserList);

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
        List<Seat> seatList = new ArrayList<>();
        for(int i = 1; i<= 50; i++){
            seatList.add(Seat.builder().seatNo(i).price(10_000L).concertSchedule(newConcertSchedule).build());
        }
        seatList = seatRepository.saveAll(seatList);

        seat = seatList.get(1);
        userList = newUserList;
        concertSchedule = newConcertSchedule;
    }


}
