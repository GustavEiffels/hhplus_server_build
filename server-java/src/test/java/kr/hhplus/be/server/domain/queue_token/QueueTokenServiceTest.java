package kr.hhplus.be.server.domain.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @Mock
    private QueueTokenRepository repository;

    @InjectMocks
    private QueueTokenServiceImpl queueTokenService;


// isValidAndActive
    @DisplayName("대기열 토큰 아이디 입력 시, 존재하지 않은 대기열 토큰 아이디를 입력 하면 ErrorCode : NOT_FOUND_QUEUE_TOKEN 가 발생한다.")
    @Test
    void isValidAndActive_Test_00(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.NOT_FOUND_QUEUE_TOKEN,exception.getErrorStatus());
    }


    @DisplayName("대기열 토큰 소유자와 입력한 사용자가 불일치 하는 경우, ErrorCode : NOT_MATCHED_WITH_USER 가 발생한다.")
    @Test
    void isValidAndActive_Test_01(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User       user       = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(2L); // userId = 10 != 2L

        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.NOT_MATCHED_WITH_USER,exception.getErrorStatus());
    }


    @DisplayName("만료된 대기열 토큰 아이디를 입력하는 경우, ErrorCode : EXPIRE_QUEUE_TOKEN 가 발생한다.")
    @Test
    void isValidAndActive_Test_02(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User user = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(queueToken.getExpireAt()).thenReturn(LocalDateTime.now().minusHours(3)); // 지금으로 부터 3시간 전을 만료 시간으로 설정


        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.EXPIRE_QUEUE_TOKEN,exception.getErrorStatus());
    }


    @DisplayName("아직 활성화 되지 않은 대기열 토큰 아이디를 isValidAndActive에 입력하는 경우, false 를 반환 받는다.")
    @Test
    void isValidAndActive_Test_03(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User user = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(queueToken.getExpireAt()).thenReturn(LocalDateTime.now().plusHours(3));
        when(queueToken.getStatus()).thenReturn(QueueTokenStatus.WAIT);


        // when & then
        Assertions.assertFalse(queueTokenService.isValidAndActive(queueTokenId,userId));
    }

// expired
    @DisplayName("존재하는 대기열 토큰을 입력 했을 때, 토큰의 만료시간은 현재 시간 보다 과거의 시간으로 설정이 된다.")
    @Test
    void expired_Test_00(){
        // given
        User user = User.create("test");
        QueueToken queueToken = QueueToken.create(user);
        queueToken.activate();

        Long tokenId = 1L;
        when(repository.findById(tokenId)).thenReturn(Optional.of(queueToken));

        // when
        queueTokenService.expired(tokenId);

        // then
        Assertions.assertTrue(queueToken.getExpireAt().isBefore(LocalDateTime.now()));
    }

//    activate
    @DisplayName("최대 활성화 가능한 토큰 수가 20 이고, 현재 활성화 된 토큰 수는 18 일때 2개의 토큰은 활성화 상태가 된다.")
    @Test
    void activate_Test_00(){
        //given
        Long maxTokenCnt = 20L;
        QueueToken queueToken1 = QueueToken.create(User.create("test1"));
        QueueToken queueToken2 = QueueToken.create(User.create("test2"));
        List<QueueToken> queueTokenList = new ArrayList<>();
        queueTokenList.add(queueToken1);
        queueTokenList.add(queueToken2);
        when(repository.countActiveTokens()).thenReturn(18L);
        when(repository.findTokensToActivate(2L)).thenReturn(queueTokenList);

        // when
        queueTokenService.activate();

        // then
        Assertions.assertEquals(QueueTokenStatus.ACTIVE,queueToken1.getStatus());
        Assertions.assertEquals(QueueTokenStatus.ACTIVE,queueToken2.getStatus());
    }

}