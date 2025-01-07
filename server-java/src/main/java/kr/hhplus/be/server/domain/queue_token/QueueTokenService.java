package kr.hhplus.be.server.domain.queue_token;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenService {
    private final QueueTokenRepository repository;


    public QueueToken createToken(QueueToken queueToken){
        return repository.create(queueToken);
    }

}
