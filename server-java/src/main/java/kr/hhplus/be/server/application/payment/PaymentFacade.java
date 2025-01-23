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
    private final UserService userService;
    private final ReservationService reservationService;
    private final QueueTokenService queueTokenService;
    private final PointHistoryService pointHistoryService;


    @Transactional // 예약 조회 시 경헙 발생 :
    public PaymentFacadeDto.PaymentResult pay(PaymentFacadeDto.PaymentParam param){

        // 예약 lock : 에약 상태로 변경
        List<Reservation> reservations = reservationService.reserve(param.reservationIds(), param.userid());

        // 총 예약 금액을 구함
        Long totalAmount = reservationService.totalAmount(reservations);

        // 사용자 lock
        User user = userService.findUser(param.userid());

        // user point 차감 -> 여기서 금액 모자르면 예외 발생
        user.pointTransaction(-totalAmount);

        // Payments 생성
        List<Payment> paymentList = reservations.stream()
                .map(reservation -> Payment.create(reservation.getAmount(),reservation)).toList();

        paymentList = paymentService.create(paymentList);

        // History 생성
        List<PointHistory> historyList = paymentList.stream()
                .map(payment -> PointHistory.createUse(payment.getAmount(),user,payment)).toList();

        pointHistoryService.create(historyList);
        queueTokenService.expired(param.tokenId());

        return  PaymentFacadeDto.PaymentResult.from(paymentList);
    }
}
