package kr.hhplus.be.server.domain.queue_token;


public interface QueueTokenService {

    void activate(Long tokenCnt);

// REDIS
    String createToken(Long userId);

    Boolean isValidAndActive(Long userId, String tokenId);

    void expired(String tokenId);

    void expireToken();

}
