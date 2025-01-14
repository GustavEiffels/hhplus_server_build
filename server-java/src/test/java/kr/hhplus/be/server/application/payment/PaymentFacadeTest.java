package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReservationFacadeDto;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.queue_token.QueueTokenStatus;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentFacadeTest {
    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;


    @Autowired
    TokenJpaRepository tokenJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    ReservationFacade reservationFacade;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    @Autowired
    PaymentFacade paymentFacade;


    @Test
    void 사용자가_만원짜리_티켓_3장을_구매하면_사용자의잔액은_3만원줄어있고_좌석은_reserved_로변경되어있고_토큰은만료됨(){
        // given
        // 유저생성
        User user   = User.create("test");
        user.pointTransaction(300_000);
        userJpaRepository.save(user);

        // 토큰 생성
        QueueTokenFacadeDto.CreateResult createTokenResult = queueTokenFacade.create(new QueueTokenFacadeDto.CreateParam(user.getId()));

        // 토큰 활성화
        queueTokenFacade.activate(new QueueTokenFacadeDto.ActivateParam(20L));


        // 콘서트 생성
        Concert concert = concertJpaRepository.save(Concert.builder().performer("쇼맨").title("티거").build());
        // 콘서트 스케줄 생성
        ConcertSchedule schedule = ConcertSchedule.builder()
                .concert(concert)
                .showTime(LocalDateTime.now().plusHours(10))
                .reserveStartTime(LocalDateTime.now().minusHours(2))
                .reserveEndTime(LocalDateTime.now().plusHours(3)).build();

        scheduleJpaRepository.save(schedule);

        // 좌석 생성
        List<Seat> seats = new ArrayList<>();
        for(int i = 1; i<= 50; i++){
            Seat seat = Seat.builder().seatNo(i).concertSchedule(schedule).price(10_000L).build();
            seats.add(seat);
        }

        List<Seat> reserveSeats = seatJpaRepository.saveAll(seats);
        List<Long> seatIds      = new ArrayList<>();
        for(Seat s : reserveSeats){
            if(s.getSeatNo() < 4){
                seatIds.add(s.getId());
            }
        }
        ReservationFacadeDto.ReservationResult reservationResult =
                reservationFacade.reservation(new ReservationFacadeDto.ReservationParam(schedule.getId(),seatIds,user.getId()));

        // 에약 생성
        List<Long> reservationIds = new ArrayList<>();
        for(ReservationFacadeDto.ReservationInfo info  : reservationResult.reservationInfoList() ){
            reservationIds.add(info.reservationId());
        }

        // when
        PaymentFacadeDto.PaymentResult result =
                paymentFacade.pay(new PaymentFacadeDto.PaymentParam(reservationIds, user.getId(), createTokenResult.tokenId()));


        // then
        assertEquals(3,result.paymentInfoList().size(),"결제 내역 개수 3개 ");
        assertEquals(270_000, userJpaRepository.findById(user.getId()).get().getPoint(), "사용자의 잔액 270,000");

        seatIds.forEach(id ->{
            assertEquals(SeatStatus.RESERVED,seatJpaRepository.findById(id).get().getStatus(),"id : "+id+" 인 좌석 상태 RESERVED");
        });
        reservationIds.forEach(id->{
            assertEquals(ReservationStatus.Reserved,reservationJpaRepository.findById(id).get().getStatus(),"id : "+id+" 인 예약상태 RESERVED");
        });
        assertTrue(tokenJpaRepository.findById(createTokenResult.tokenId()).get().getExpireAt().isBefore(LocalDateTime.now()),"토큰 만료됨 ");



    }

}