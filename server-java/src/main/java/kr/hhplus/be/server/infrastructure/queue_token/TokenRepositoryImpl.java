package kr.hhplus.be.server.infrastructure.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl {
    private final TokenJpaRepository tokenJpaRepository;

    public QueueToken create(QueueToken queueToken){
        return tokenJpaRepository.save(queueToken);
    }
}
