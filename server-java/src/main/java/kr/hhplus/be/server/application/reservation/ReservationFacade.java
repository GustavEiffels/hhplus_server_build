package kr.hhplus.be.server.application.reservation;


import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
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


    /**
     * USECASE 3
     * @param param
     */
    @Transactional
    public ReservationFacadeDto.ReservationResult reservation(ReservationFacadeDto.ReservationParam param){

        // userid 를 가지고 유저를 조회
        User findUser = userService.findUser(param.userId());


        // seatId 리스트를 받아서 id 로 list 를 받아서 조회 -> 비관적 락 적용 => findAllReserveAbleWithLock
        List<Seat> seatList = seatService.findReservableForUpdate(param.seatIdList());


        List<Reservation> createdReservations = seatList.stream()
                .map(item -> {
                    item.reserve();
                    return Reservation.create(findUser,item);
                })
                .toList();

        reservationService.create(createdReservations);

        if(seatService.findReservable(param.scheduleId()).isEmpty()){
            ConcertSchedule schedule = concertScheduleService.findScheduleForUpdate(param.scheduleId());
            schedule.updateReserveStatus(false);
        }
        return ReservationFacadeDto.ReservationResult.from(createdReservations);
    }


    /**
     * 좌석 만료 스케줄
     */
    @Transactional
    public void expire(){
        // 1. 예약 만료 시키고 연관된 좌석들 id 반환
        List<Long> seatIds = reservationService.expireAndReturnSeatList();

        // 2. 예약 만료된 좌석들이 존재하면
        if(!seatIds.isEmpty()){
            // 1. 좌석들의 상태 : reserved -> reservable, 연관된 스케줄 아이디 리스트 반환
            // 2. 콘서트 스케줄들의 예약 가능 상태 : true 로 변환
            concertScheduleService.reservable( seatService.reservableAndReturnSchedules(seatIds) );
        }
    }
}
