package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.User;

import java.util.Optional;

public interface UserJpaRepositoryCustom {

    Optional<User> findByUserId(Long userId);
}
