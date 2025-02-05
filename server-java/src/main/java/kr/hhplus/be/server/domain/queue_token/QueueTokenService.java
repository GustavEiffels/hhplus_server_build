package kr.hhplus.be.server.domain.queue_token;


public interface QueueTokenService {

    void activate(Long maxTokenCnt);

    void expired(Long tokenId);

    QueueToken createToken(QueueToken queueToken);

    Boolean isValidAndActive(Long queueTokenId, Long userId);

    String createToken(Long userId);
}
