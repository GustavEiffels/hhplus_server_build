package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationFacadeTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    ReservationFacade reservationFacade;


    @Autowired
    SeatService seatService;


    Concert concert0;
    Concert concert1;

    ConcertSchedule schedule0;

    User user;

    List<Seat> reservableSeats;

    @BeforeEach
    void setUp_공연0은예약가능_18_예약불가능_12_공연1_예약가능_12_예약_불가능_18_스케줄0_좌석50개_짝수_점유_홀수_예약가능(){

        user = userJpaRepository.save(User.create("test"));

        concert0 = Concert.builder().title("서커스0").performer("김광대").build();
        concertJpaRepository.save(concert0);

        concert1 = Concert.builder().title("서커스1").performer("이광대").build();
        concertJpaRepository.save(concert1);

        List<ConcertSchedule> concertScheduleList0 = new ArrayList<>();
        List<ConcertSchedule> concertScheduleList1 = new ArrayList<>();

        for(int i = 1; i<=30; i++){
            if(i < 19){
                concertScheduleList0.add(
                        ConcertSchedule.builder().concert(concert0)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().minusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
                concertScheduleList1.add(
                        ConcertSchedule.builder().concert(concert1)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().plusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
            }
            else{
                concertScheduleList1.add(
                        ConcertSchedule.builder().concert(concert1)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().minusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
                concertScheduleList0.add(
                        ConcertSchedule.builder().concert(concert0)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().plusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
            }

        }

        scheduleJpaRepository.saveAll(concertScheduleList0);
        scheduleJpaRepository.saveAll(concertScheduleList1);

        schedule0 = concertScheduleList0.get(0);

        List<Seat> seatList = new ArrayList<>();
        for(int i = 1; i<=50; i++){
            Seat newSeat = Seat.builder().seatNo(i).concertSchedule(schedule0)
                    .price(300_000L).build();

            if(i % 2 == 0){
                newSeat.reserve();
            }
            seatList.add(newSeat);
        }
        seatJpaRepository.saveAll(seatList);
        reservableSeats = new ArrayList<>();
        for(Seat seat : seatList){
            if(seat.getStatus().equals(SeatStatus.RESERVABLE)){
                reservableSeats.add(seat);
            }
        }
    }


// 좌석 예약
    @Test
    void _4_건의_홀수_번호로_예약시_전체_예약가능한_좌석의수가_21건이_된다(){
        //given
        Long scheduleId = schedule0.getId();
        Long userId     = user.getId();
        List<Long> seatIds = new ArrayList<>();
        for(int i = 0; i<4; i++){
            seatIds.add(reservableSeats.get(i).getId());
        }
        ReservationFacadeDto.ReservationParam param =  new ReservationFacadeDto.ReservationParam(scheduleId,seatIds,userId);

        // when
        ReservationFacadeDto.ReservationResult result = reservationFacade.reservation(param);

        // then
        assertEquals(4,result.reservationInfoList().size());
        assertEquals(21, seatService.findReservable(scheduleId).size());
    }

// 좌석 점유 만료
    @Test
    void _2건의_예약이_만료시간을_초과한경우_해당_예약은_만료처리가되고_점유됐던_좌석은_예약가능한상태가_된다(){
        // given
        Seat        seat1        = reservableSeats.get(1);
        Seat        seat2        = reservableSeats.get(2);
        Long        scheduleId   = schedule0.getId();
        Long        userId       = user.getId();
        List<Long>  seatIds      = new ArrayList<>();
        seatIds.add(seat1.getId());
        seatIds.add(seat2.getId());
        ReservationFacadeDto.ReservationResult result = reservationFacade.reservation(new ReservationFacadeDto.ReservationParam(scheduleId,seatIds,userId));

        List<Long> reservationIds = result.reservationInfoList().stream()
                .map(ReservationFacadeDto.ReservationInfo::reservationId)
                .toList();

        Reservation reservation0 = reservationJpaRepository.findById(reservationIds.get(0)).get();
        Reservation reservation1 = reservationJpaRepository.findById(reservationIds.get(1)).get();
        reservation0.expired();
        reservation1.expired();

        reservationJpaRepository.save(reservation0);
        reservationJpaRepository.save(reservation1);

        // when
        reservationFacade.expire();

        // then
        Reservation reservation0_1 = reservationJpaRepository.findById(reservationIds.get(0)).get();
        Reservation reservation1_1 = reservationJpaRepository.findById(reservationIds.get(1)).get();
        assertEquals(ReservationStatus.EXPIRED,reservation0_1.getStatus());
        assertEquals(ReservationStatus.EXPIRED,reservation1_1.getStatus());

    }

}