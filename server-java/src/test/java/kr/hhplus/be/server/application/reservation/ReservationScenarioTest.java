package kr.hhplus.be.server.application.reservation;

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

@SpringBootTest
public class ReservationScenarioTest {
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
    List<Seat> newSeatList;

    ConcertSchedule concertSchedule;

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

        newSeatList = seatList;
        userList = newUserList;
        concertSchedule = newConcertSchedule;
    }


    @DisplayName("""
                 다른 사용자가 같은 예약 자리를 예약하려고 할때
                 늦게 자리를 예약하려는 사람은 ErrorCode : NOT_RESERVABLE_DETECTED 가 발생한다.
            """)
    @Test
    void reservation(){
        // given
        Long scheduleId = concertSchedule.getId();
        Long userId1    = userList.get(1).getId();
        Long userId2    = userList.get(2).getId();
        List<Long> sameSeatList = new ArrayList<>();
        sameSeatList.add(newSeatList.get(1).getId());
        // user1 이 동일한 좌석 먼제 예약함
        ReservationFacadeDto.ReservationParam param1 = new ReservationFacadeDto.ReservationParam(scheduleId,sameSeatList,userId1);
        reservationFacade.reservation(param1);

        // when -> reservation same seat
        ReservationFacadeDto.ReservationParam param2 = new ReservationFacadeDto.ReservationParam(scheduleId,sameSeatList,userId2);
        BusinessException exception = Assertions.assertThrows(BusinessException.class,()->{
            reservationFacade.reservation(param2);
        });

        // then
        Assertions.assertEquals(ErrorCode.NOT_RESERVABLE_DETECTED,exception.getErrorStatus());
    }

}
