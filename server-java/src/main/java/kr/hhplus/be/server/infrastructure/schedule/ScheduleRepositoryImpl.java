package kr.hhplus.be.server.infrastructure.schedule;


import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ConcertScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    @Override
    public List<ConcertSchedule> findReservableScheduleList(Long concertId, int page) {
        return jpaRepository.findAllReserveAbleByConcertId(concertId,page);
    }
}
