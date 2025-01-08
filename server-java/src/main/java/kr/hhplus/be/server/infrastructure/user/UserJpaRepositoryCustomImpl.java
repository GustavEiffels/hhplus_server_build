package kr.hhplus.be.server.infrastructure.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.user.QUser;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static kr.hhplus.be.server.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserJpaRepositoryCustomImpl implements UserJpaRepositoryCustom{
    private final JPAQueryFactory dsl;
}
