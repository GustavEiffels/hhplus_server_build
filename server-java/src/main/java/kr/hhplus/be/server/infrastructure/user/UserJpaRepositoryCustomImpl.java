package kr.hhplus.be.server.infrastructure.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.QUser;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static kr.hhplus.be.server.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserJpaRepositoryCustomImpl implements UserJpaRepositoryCustom{
    private final JPAQueryFactory dsl;

    @Override
    public Optional<User> findByIdWithLock(Long userId) {
        return Optional.ofNullable(
                dsl.selectFrom(user)
                        .where(user.id.eq(userId))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .fetchOne());
    }

    @Override
    public Optional<User> findByIdWithLockSetLockTime(Long userId) {
        return Optional.ofNullable(
                dsl.selectFrom(user)
                        .where(user.id.eq(userId))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .setHint("javax.persistence.lock.timeout", 0)
                        .fetchOne());
    }
}
