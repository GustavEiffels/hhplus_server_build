package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.point_history.PointHistoryService;
import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
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
public class PaymentFacade {
    private final PaymentService paymentService;
    private final SeatService seatService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final QueueTokenService queueTokenService;
    private final PointHistoryService pointHistoryService;


    @Transactional
    public PaymentFacadeDto.PaymentResult pay(PaymentFacadeDto.PaymentParam param){

        // 사용자 lock
        User user = userService.findByIdWithLock(param.userid());


        // 예약 lock : 에약 상태로 변경
        List<Reservation> reservations = reservationService.findByIdsWithUseridAndLock(param.reservationIds(), param.userid())
                .stream()
                .peek(item -> item.updateStatus(ReservationStatus.Reserved)) // 상태를 업데이트
                .toList();


        // 좌석 id 추출 -> lock 이 아님..
        List<Long> seatIds = reservations.stream()
                .map(reservation -> reservation.getSeat().getId())
                .collect(Collectors.toList());


        // 좌석 lock : 예약 상태로 변경
        if(!seatIds.isEmpty()){
            seatService.findAllByIdsWithLock(seatIds).forEach(item->{
                item.updateStatus(SeatStatus.RESERVED);
            });
        }

        // 총 예약 금액을 구함
        Long totalAmount = reservations.stream()
                .map(Reservation::getAmount)
                .reduce(0L, Long::sum);

        // user point 차감 -> 여기서 금액 모자르면 예외 발생
        user.pointTransaction(-totalAmount);

        // Payments 생성
        List<Payment> paymentList = reservations.stream()
                .map(reservation ->
                    Payment.builder()
                            .amount(reservation.getAmount())
                            .reservation(reservation).build()).toList();


        paymentList = paymentService.create(paymentList);

        // History 생성
        List<PointHistory> historyList = paymentList.stream()
                .map(payment ->
                        PointHistory.createUse(payment.getAmount(),user,payment)).toList();

        pointHistoryService.create(historyList);

        queueTokenService.expired(param.tokenId());

        return  PaymentFacadeDto.PaymentResult.from(paymentList);
    }
}
