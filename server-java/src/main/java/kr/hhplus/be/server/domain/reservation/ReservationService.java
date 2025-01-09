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


    public List<Reservation> findExpiredWithLock(){
        return repository.findExpiredWithLock();
    }

    public List<Reservation> findByIdsWithUseridAndLock(List<Long> reservationIds, Long userId){

        List<Reservation> reservations = repository.findByIdsWithLock(reservationIds);  // 예약 조회

        if (reservations.isEmpty()) {
            throw new BusinessException(ErrorCode.Entity, "예약이 존재하지 않습니다.");
        }

        reservations.forEach(item -> {
            // 사용자 ID가 일치하는지 확인
            if (!Objects.equals(item.getUser().getId(), userId)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "사용자와 예약자가 일치하지 않습니다.");
            }

            // 만료된 예약인지 확인
            if (item.getExpiredAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.Entity, "만료된 예약이 존재합니다.");
            }
        });

        return reservations;
    }

}
