package kr.hhplus.be.server.application.reservation;


import kr.hhplus.be.server.common.config.redis.DistributedLock;
import kr.hhplus.be.server.domain.event.reservation.ReservationEventPublisher;
import kr.hhplus.be.server.domain.event.reservation.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final UserService userService;
    private final SeatService seatService;
    private final ConcertScheduleService concertScheduleService;
    private final ReservationService reservationService;
    private final ReservationEventPublisher reservationEventPublisher;

    /**
     * USECASE 3
     * @param param
     */
    @Transactional
    @DistributedLock(lockNm = "reservation-lock:", waitTime = 0L, leaseTime = 1000L)
    public ReservationFacadeDto.ReservationResult reservation(ReservationFacadeDto.ReservationParam param){

        // 1. 예약할 좌석들을 조회 : lock
        List<Seat> seatList = seatService.findReservableForUpdate(param.seatIdList());

        // 2. 사용자 조회
        User findUser        = userService.findUser(param.userId());

        // 3. 예약할 좌석들을 기반으로 예약을 생성
        List<Reservation> createdReservations = seatList.stream()
                .map(item -> {
                    item.reserve();
                    return Reservation.create(findUser,item);
                })
                .toList();

        // 4. 예약 생성
        createdReservations = reservationService.create(createdReservations);


        // 5. 해당 콘서트 스케줄에 더이상 예약 가능한 좌석이 없는 경우 -> 예약 불가능 상태로 변환
        if(seatService.findReservable(param.scheduleId()).isEmpty()){
            concertScheduleService.changeUnReservable(param.scheduleId());
        }

        // 6. OutBox 생성
        OutBox outBox = OutBox.create("create_reservation",createdReservations);

        // 7. EVENT 로 발행
        reservationEventPublisher.success(new ReservationSuccessEvent(outBox));

        return ReservationFacadeDto.ReservationResult.from(createdReservations);
    }


    /**
     * 좌석 만료 스케줄
     */
    @Transactional
    public void expire(){
        // 1. 예약 만료 시키고 연관된 좌석들 id 반환
        List<Long> seatIds = reservationService.expireAndReturnSeatIdList();

        // 2. 예약 만료된 좌석들이 존재하면
        if(!seatIds.isEmpty()){
            // 1. 좌석들의 상태 : reserved -> reservable, 연관된 스케줄 아이디 리스트 반환
            // 2. 콘서트 스케줄들의 예약 가능 상태 : true 로 변환
            concertScheduleService.changeReservable( seatService.reservableAndReturnSchedules(seatIds) );
        }
    }
}
