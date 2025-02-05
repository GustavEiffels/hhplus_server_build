package kr.hhplus.be.server.infrastructure.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Profile("local")
public class TokenRepositoryImpl implements QueueTokenRepository {
    private final TokenJpaRepository tokenJpaRepository;

    private final TokenRedisRepository redisRepository;



    @Override
    public List<QueueToken> findTokensToActivate(long activateCnt) {
        return tokenJpaRepository.findTokensToActivate(activateCnt);
    }

    // ACTIVE 토큰 개수 구하기
    @Override
    public long countActiveTokens() {
        return tokenJpaRepository.countActiveTokens();
    }


    // 토큰 생성
    @Override
    public QueueToken create(QueueToken queueToken){
        return tokenJpaRepository.save(queueToken);
    }


    /**
     * 토큰아이디로
     * 토큰과 유저 정보를 페치 조인하여 함께 가져오는 함수
     * @param queueTokenId
     * @return
     */
    @Override
    public Optional<QueueToken> findByIdWithUser(Long queueTokenId){
        return tokenJpaRepository.findByIdWithUser(queueTokenId);
    }

    @Override
    public Optional<QueueToken> findById(Long tokenId) {
        return tokenJpaRepository.findById(tokenId);
    }

    @Override
    public void createMappingTable(String tokenId, Long userId) {

    }

    @Override
    public void insertTokenToWaitingArea(String tokenId) {

    }

    @Override
    public Long findUserIdByTokenId(String tokenId) {
        return null;
    }

    @Override
    public Long findWaitingTokenByTokenId(String tokenId) {
        return null;
    }

    @Override
    public Boolean isActiveToken(String tokenId) {
        return null;
    }
}
