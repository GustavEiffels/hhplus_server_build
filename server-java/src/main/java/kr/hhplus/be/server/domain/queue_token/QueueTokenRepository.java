package kr.hhplus.be.server.domain.queue_token;

import java.util.Optional;

public interface QueueTokenRepository {
    QueueToken create(QueueToken queueToken);

    Optional<QueueToken> findById(Long queueTokenId);
}
