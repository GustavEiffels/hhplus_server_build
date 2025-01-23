package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Profile("dev")
public class UserRepositoryDevImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findByIdWithLockSetLockTime(userId);
    }

    @Override
    public Optional<User> findByIdWithLock(Long userId) {
        return jpaRepository.findByIdWithLock(userId);
    }
}
