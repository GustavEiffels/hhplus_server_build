package kr.hhplus.be.server.infrastructure.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.concert.QConcert.concert;
import static kr.hhplus.be.server.domain.schedule.QConcertSchedule.concertSchedule;

@RequiredArgsConstructor
public class ScheduleJpaRepositoryCustomImpl implements ScheduleJpaRepositoryCustom {
    private final JPAQueryFactory dsl;

    @Override
    public List<ConcertSchedule> findAllReserveAbleByConcertId(Long concertId, int page) {
        LocalDateTime now = LocalDateTime.now();
        long pagingSize    = 10;

        return dsl.selectFrom(concertSchedule)
                .innerJoin(concertSchedule.concert, concert)
                .where(
                        concert.id.eq(concertId)
                                .and(concertSchedule.isReserveAble.isTrue())
                                .and(concertSchedule.reservation_start.loe(now))  // 예약 시작 시간은 현재 시간보다 작거나 같아야 함
                                .and(concertSchedule.reservation_end.goe(now))  // 예약 종료 시간은 현재 시간보다 크거나 같아야 함
                )
                .offset(page * pagingSize)
                .limit(pagingSize)
                .fetch();
    }
}
