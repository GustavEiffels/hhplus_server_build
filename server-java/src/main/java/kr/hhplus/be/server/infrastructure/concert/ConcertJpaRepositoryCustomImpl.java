package kr.hhplus.be.server.infrastructure.concert;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcertJpaRepositoryCustomImpl implements ConcertJpaRepositoryCustom {
    private final JPAQueryFactory dsl;
}
