package kr.hhplus.be.server.application.reservation;


import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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


        // seatid 리스트를 받아서 id 로 list 를 받아서 조회 -> 비관적 락 적용 => findAllReserveAbleWithLock
        List<Seat> seatList = seatService.findAllReserveAbleWithLock(param.seatIdList());


        // 모든 seat 을 occupied, expiredAt 을 사용하여 5분 추가
        List<Reservation> createdReservations = seatList.stream()
                .map(item -> {
                    item.updateStatus(SeatStatus.OCCUPIED);
                    Reservation reservation = Reservation.builder()
                            .user(findUser)
                            .seat(item)
                            .build();
                    return reservationService.create(reservation);
                })
                .toList();

        if(seatService.findByScheduleId(param.scheduleId()).isEmpty()){
            ConcertSchedule schedule = concertScheduleService.findByIdWithLock(param.scheduleId());
            schedule.updateReserveStatus(false);
        }
        return ReservationFacadeDto.ReservationResult.from(createdReservations);
    }


    /**
     * 토큰 만료 스케줄
     */
    @Transactional
    public void expire(){
        List<Long> seatIds = reservationService.findExpiredWithLock().stream()
                .map(item -> {
                    item.updateStatus(ReservationStatus.Expired);
                    return item.getSeat().getId();
                })
                .toList();

        if(!seatIds.isEmpty()){
            List<Long> concertScheduleIds = seatService.findAllByIdsWithLock(seatIds).stream()
                    .map(item -> {
                        item.updateStatus(SeatStatus.RESERVABLE);
                        return item.getConcertSchedule().getId();
                    })
                    .toList();

            concertScheduleService.findByIdsWithLock(concertScheduleIds)
                    .forEach(item->{
                        item.updateReserveStatus(true);
                    });
        }
    }
}
