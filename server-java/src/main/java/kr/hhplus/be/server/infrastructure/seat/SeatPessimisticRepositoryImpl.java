package kr.hhplus.be.server.infrastructure.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Profile("pessimistic")
public class SeatPessimisticRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    @Override
    public List<Seat> findByIdsWithLock(List<Long> seatIds) {
        return jpaRepository.findByIdsWithLock(seatIds);
    }


    @Override
    public List<Seat> findByScheduleId(Long concertScheduleId) {
        return jpaRepository.findByScheduleId(concertScheduleId);
    }
}
