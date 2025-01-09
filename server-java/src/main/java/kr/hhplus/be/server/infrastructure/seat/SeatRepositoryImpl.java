package kr.hhplus.be.server.infrastructure.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    @Override
    public List<Seat> findAllByIdsWithLock(List<Long> seatIds) {
        return jpaRepository.findAllByIdsWithLock(seatIds);
    }

    @Override
    public List<Seat> findByScheduleId(Long concertScheduleId) {
        return jpaRepository.findByScheduleId(concertScheduleId);
    }
}
