package kr.hhplus.be.server.domain.user;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @DisplayName("사용자 조회 시, 사용자가 존재하지 않으면 BusinessException 이 발생한다.")
    @Test
    void findUser_Test_00(){
        // given
        Long userId = 1L;
        Mockito.when(repository.findById(userId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> userService.findUser(userId));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }


    @DisplayName("포인트 충전 시, 존재하지 않은 사용자 Id 를 받으면, BusinessExcetpion 이 발생한다.")
    @Test
    void chargePoints_Test_00(){
        // given
        Long userId = 1L;
        Long amount = 100_000L;
        Mockito.when(repository.findByIdWithLock(userId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> userService.chargePoints(userId,amount));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }

    @DisplayName("포인트 충전 시, 포인트 정책에 맞지 않는 포인트를 넣으면, BusinessException 이 발생한다.")
    @Test
    void chargePoints_Test_01(){
        // given
        Long userId = 1L;
        Long amount = 100_000_001L;
        User user = User.builder().name("김연습").build();;
            Mockito.when(repository.findByIdWithLock(userId)).thenReturn(Optional.of(user));

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> userService.chargePoints(userId,amount));

    }

}