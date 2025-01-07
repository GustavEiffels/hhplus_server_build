package kr.hhplus.be.server.infrastructure.queue_token;


import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.queue_token.QQueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueToken;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static kr.hhplus.be.server.domain.queue_token.QQueueToken.queueToken;

@RequiredArgsConstructor
public class TokenJpaRepositoryCustomImpl implements TokenJpaRepositoryCustom{
    private final JPAQueryFactory dsl;

    @Override
    public Optional<QueueToken> findByQueueTokenId(Long queueTokenId) {
        return Optional.ofNullable(
                dsl.selectFrom(queueToken)
                        .join(queueToken.user).fetchJoin()
                        .where(queueToken.id.eq(queueTokenId))
                        .fetchOne()
        );
    }
}
