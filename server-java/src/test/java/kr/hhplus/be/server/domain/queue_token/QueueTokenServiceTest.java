package kr.hhplus.be.server.domain.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @Mock
    private QueueTokenRepository repository;

    @InjectMocks
    private QueueTokenService queueTokenService;

    @Test
    void 존재하지_않은_대기열토큰_아이디를_입력_받으면_BaseException_ErrorCode_Repository_가_발생한다(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;
        QueueToken queueToken = mock(QueueToken.class);
        User       user = mock(User.class);

        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }


    @Test
    void 대기열토큰의_사용자와_입력된_사용자가_일치하지_않을경우_BaseException_ErrorCode_Repository_가_발생한다(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User user = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(2L); // userId = 10 != 2L

        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }


    @Test
    void 만료된_대기열_토큰일_경우_BaseException_ErrorCode_Repository_가_발생한다(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User user = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(queueToken.getExpireAt()).thenReturn(LocalDateTime.now().minusHours(3)); // 지금으로 부터 3시간 전이 만료 시간전인 경우


        // when
        BusinessException exception =
                Assertions.assertThrows(BusinessException.class, () -> queueTokenService.isValidAndActive(queueTokenId,userId));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }


    @Test
    void 대기열토큰의_상태가_wait_인_경우_false_를_반환(){
        // given
        Long queueTokenId = 10L;
        Long userId       = 10L;

        QueueToken queueToken = mock(QueueToken.class);
        User user = mock(User.class);
        when(repository.findByIdWithUser(queueTokenId)).thenReturn(Optional.of(queueToken));
        when(queueToken.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(queueToken.getExpireAt()).thenReturn(LocalDateTime.now().plusHours(3));
        when(queueToken.getStatus()).thenReturn(QueueTokenStatus.Wait);


        // when & then
        Assertions.assertFalse(queueTokenService.isValidAndActive(queueTokenId,userId));
    }
}