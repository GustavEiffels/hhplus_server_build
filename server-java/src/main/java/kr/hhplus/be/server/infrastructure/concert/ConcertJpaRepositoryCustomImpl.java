package kr.hhplus.be.server.infrastructure.concert;


import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.concert.Concert;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static kr.hhplus.be.server.domain.concert.QConcert.concert;

@RequiredArgsConstructor
public class ConcertJpaRepositoryCustomImpl implements ConcertJpaRepositoryCustom {
    private final JPAQueryFactory dsl;

    @Override
    public Optional<Concert> findByConcertId(Long concertId) {
        return Optional.ofNullable(
                dsl.selectFrom(concert).where(concert.id.eq(concertId))
                        .fetchOne());
    }
}
