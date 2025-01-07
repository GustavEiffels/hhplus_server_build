package kr.hhplus.be.server.infrastructure.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements QueueTokenRepository {
    private final TokenJpaRepository tokenJpaRepository;

    public QueueToken create(QueueToken queueToken){
        return tokenJpaRepository.save(queueToken);
    }

    public Optional<QueueToken> findById(Long queueTokenId){
        return tokenJpaRepository.findByQueueTokenId(queueTokenId);
    }
}
