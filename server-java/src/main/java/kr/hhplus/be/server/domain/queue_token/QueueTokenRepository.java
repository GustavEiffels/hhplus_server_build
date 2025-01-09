package kr.hhplus.be.server.domain.queue_token;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository {

    List<QueueToken> findTokensToActivate(long activateCnt);

    /**
     * ACTIVE 인 토큰 개수 반환
     * @return
     */
    long countActiveTokens();

    /**
     * 토큰 생성
     * @param queueToken
     * @return
     */
    QueueToken create(QueueToken queueToken);

    /**
     * 토큰 조회
     * @param queueTokenId
     * @return
     */
    Optional<QueueToken> findByIdWithUser(Long queueTokenId);

    Optional<QueueToken> findById(Long tokenId);


}
