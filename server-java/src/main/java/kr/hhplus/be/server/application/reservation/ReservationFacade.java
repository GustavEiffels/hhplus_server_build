package kr.hhplus.be.server.application.reservation;


import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
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
    private final ReservationService reservationService;

    @Transactional
    public void reservation(List<Long> seatIdList, Long userId){
        // userid 를 가지고 유저를 조회
        User findUser = userService.findById(userId);

        // seatid 리스트를 받아서 id 로 list 를 받아서 조회 -> 비관적 락 적용 => findAllReserveAbleWithLock
        List<Seat> seatList = seatService.findAllReserveAbleWithLock(seatIdList);


        // 모든 seat 을 occupied, expiredAt 을 사용하여 5분 추가
        seatList.forEach(item->{
            item.updateStatus(SeatStatus.OCCUPIED);
            reservationService.create(Reservation.builder()
                    .user(findUser)
                    .seat(item)
                    .build());
        });
    }
}
