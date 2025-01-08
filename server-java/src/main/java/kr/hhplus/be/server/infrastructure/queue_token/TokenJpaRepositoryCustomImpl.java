package kr.hhplus.be.server.infrastructure.queue_token;


import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.queue_token.QQueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.domain.queue_token.QQueueToken.queueToken;

@RequiredArgsConstructor
public class TokenJpaRepositoryCustomImpl implements TokenJpaRepositoryCustom{
    private final JPAQueryFactory dsl;

    @Override
    public List<QueueToken> findTokensToActivate(long activateCnt) {
        return dsl.selectFrom(queueToken)
                .where(queueToken.status.eq(QueueTokenStatus.Wait))
                .orderBy(queueToken.createAt.asc())
                .limit(activateCnt)
                .fetch();
    }

    @Override
    public Optional<QueueToken> findByQueueTokenId(Long queueTokenId) {
        return Optional.ofNullable(
                dsl.selectFrom(queueToken)
                        .join(queueToken.user).fetchJoin()
                        .where(queueToken.id.eq(queueTokenId))
                        .fetchOne()
        );
    }

    /**
     * 현재 활성화된 토큰의 개수를 구한다.
     * @return
     */
    @Override
    public long countActiveTokens() {
        Long count = dsl.select(queueToken.count())
                .from(queueToken)
                .where(queueToken.expireAt.after(LocalDateTime.now())
                        //.and(queueToken.status.eq(QueueTokenStatus.Active)) -> active 된 애들만 만료시간이 할당되니까..
                ).fetchOne();
        return count != null ? count : 0;
    }
}
