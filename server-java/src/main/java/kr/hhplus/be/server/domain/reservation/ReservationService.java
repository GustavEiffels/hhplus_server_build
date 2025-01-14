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

    public Reservation create(Reservation reservation){
        return repository.save(reservation);
    }

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

        List<Reservation> reservations = repository.fetchFindByIdsWithLock(reservationIds);  // 예약 조회

        if (reservations.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_RESERVATION);
        }

        reservations.forEach(item -> {
            item.isExpired();           // 만료된 예약은 아닌지 확인
            item.isReserveUser(userId); // 예약한 사용자가 일치하는 지 확인
            item.reserve();
        });

        return reservations;
    }

    /**
     * 예약 만료 시키고, 만료된 좌석 ID 를 반환
     * @return
     */
    public List<Long> expireWithSeatList(){
        return repository.findExpiredWithLock().stream()
                .map(item -> {
                    item.expired();
                    return item.getSeat().getId();
                })
                .toList();
    }

}
