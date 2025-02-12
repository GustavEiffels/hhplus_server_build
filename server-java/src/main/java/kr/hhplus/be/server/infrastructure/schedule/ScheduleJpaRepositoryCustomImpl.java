package kr.hhplus.be.server.infrastructure.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.QConcertSchedule;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                                .and(concertSchedule.reservation_start.loe(now))  // 예약 시작 시간은 현재 시간보다 작거나 같아야 함 <
                                .and(concertSchedule.reservation_end.goe(now))  // 예약 종료 시간은 현재 시간보다 크거나 같아야 함 >
                )
                .offset(page * pagingSize)
                .limit(pagingSize)
                .fetch();
    }

    @Override
    public Optional<ConcertSchedule> findByIdWithLock(Long scheduleId) {
        return Optional.ofNullable(
                dsl.selectFrom(concertSchedule)
                        .where(concertSchedule.id.eq(scheduleId))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .fetchOne()
        );
    }

    @Override
    public List<ConcertSchedule> findByIdsWithLock(List<Long> scheduleIds) {
        return dsl.selectFrom(concertSchedule)
                .where(concertSchedule.id.in(scheduleIds))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }
}
