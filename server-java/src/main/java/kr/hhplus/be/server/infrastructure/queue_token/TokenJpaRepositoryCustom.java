package kr.hhplus.be.server.infrastructure.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenJpaRepositoryCustom {

    List<QueueToken>    findTokensToActivate(long activateCnt);

    Optional<QueueToken> findByIdWithUser(Long queueTokenId);

    long countActiveTokens();
}
