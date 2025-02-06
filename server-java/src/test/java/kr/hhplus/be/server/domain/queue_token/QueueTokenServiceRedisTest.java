package kr.hhplus.be.server.domain.queue_token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceRedisTest {

    @Mock
    private QueueTokenRepository repository;

    @InjectMocks
    private QueueTokenServiceRedisImpl queueTokenService;

    @DisplayName("createToken 를 사용하면 repository 의 createMappingTable, insertTokenToWaitingArea 메소드를 각각 한번씩 호출한다.")
    @Test
    void createToken_CacheTest(){
        // given
        Long userId = 1L;

        // when
        String tokenId = queueTokenService.createToken(userId);

        Mockito.verify(repository, Mockito.times(1)).putMappingTable(Mockito.eq(tokenId),Mockito.eq(userId));
        Mockito.verify(repository, Mockito.times(1)).putWaiting(Mockito.eq(tokenId));
    }

    @DisplayName("isValidAndActive 를 사용하면 repository 의 findUserIdByTokenId, findWaitingTokenByTokenId 가 한번씩 실행된다.")
    @Test
    void isValidAndActive_CacheTest(){
        // given
        Long userId = 1L;
        String tokenId = "test";
        Mockito.when(repository.findUserIdFromMappingTable(tokenId)).thenReturn(userId);

        // when
        queueTokenService.isValidAndActive(userId,tokenId);

        Mockito.verify(repository, Mockito.times(1)).findUserIdFromMappingTable(Mockito.eq(tokenId));
        Mockito.verify(repository, Mockito.times(1)).getRankFromWaiting(Mockito.eq(tokenId));
    }

}