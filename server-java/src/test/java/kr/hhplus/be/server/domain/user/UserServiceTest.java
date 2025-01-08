package kr.hhplus.be.server.domain.user;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void 사용자_조회시_사용자가_존재하지_않으면_BusinessException_Status_Repository_가_발생한다(){
        // given
        Long userId = 1L;
        Mockito.when(repository.findById(userId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> userService.findById(userId));

        // then
        Assertions.assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }


}