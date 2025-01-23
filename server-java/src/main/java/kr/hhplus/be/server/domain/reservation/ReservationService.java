package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    /**
     * 예약 생성 - 단건
     * @param reservation
     * @return
     */
    public Reservation create(Reservation reservation){
        return repository.save(reservation);
    }

    /**
     * 예약 생성 - 복수
     * @param reservationList
     * @return
     */
    public List<Reservation> create(List<Reservation> reservationList){
        return  repository.save(reservationList);
    }

    /**
     * 입력 받은 예약들 중
     * 사용자의 예약이며, 만료되지 않은 예약이면
     * 상태 값을 pending -> reserve 로 변경
     *
     * @param reservationIds
     * @param userId
     * @return
     */
    public List<Reservation> reserve(List<Long> reservationIds, Long userId){
        // 1. 예약 아이디 리스트로 조회
        List<Reservation> reservations = repository.findByIdsWithLock(reservationIds);

        // 2. 존재하지 않은 예약일 경우 예외
        if (reservations.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_RESERVATION);
        }

        // 3. 예약 존재 시 내부에서 검증 -> 검증에 통과되면 상태를 pending-> reserve 로 변경
        reservations.forEach(item -> {
            item.isExpired();           // 만료된 예약은 아닌지 확인
            item.isReserveUser(userId); // 예약한 사용자가 일치하는 지 확인
            item.reserve();             // status : pending -> reserve
        });

        return reservations;
    }

    /**
     * 예약 만료 시키고, 만료된 좌석 ID 를 반환
     * @return
     */
    public List<Long> expireAndReturnSeatIdList(){
        // 만료된 예약들을 조회하고, 예약들의 status : pending -> expire, 연관된 좌석 id 리스트 반환
        return repository.findExpiredWithLock().stream()
                .map(item -> {
                    item.expired();                 //  예약들의 status : pending -> expire
                    return item.getSeat().getId();  //  예약된 좌석 id 반환
                })
                .toList();
    }

    public Long totalAmount(List<Reservation> reservations){
        return reservations.stream()
                .map(Reservation::getAmount)
                .reduce(0L, Long::sum);
    }

}
