package kr.hhplus.be.server.infrastructure.outbox;


import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxStatus;
import kr.hhplus.be.server.domain.outbox.QOutBox;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.outbox.QOutBox.outBox;

@RequiredArgsConstructor
public class OutBoxJpaRepositoryCustomImpl implements OutBoxJpaRepositoryCustom{

    private final JPAQueryFactory dsl;

    @Override
    public List<OutBox> findByStatusPending() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        return dsl.selectFrom(outBox)
                .where(outBox.status.eq(OutBoxStatus.PENDING)
                        .and(outBox.createAt.after(oneMinuteAgo)))
                .fetch();
    }

    @Override
    public List<OutBox> findDeleteList() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        return dsl.selectFrom(outBox)
                .where(outBox.status.ne(OutBoxStatus.PENDING)
                        .or(outBox.createAt.before(oneMinuteAgo)))
                .fetch();
    }
}
