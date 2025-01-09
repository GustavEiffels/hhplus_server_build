package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointFacadeTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired PointFacade pointFacade;


    User newUser;

    @BeforeEach
    void setUp_테스트_유저_생성_10_000_포인트_충전(){
        User testUser = User.builder().name("김연습").build();
        testUser.pointTransaction(10000L);
        newUser = userJpaRepository.save(testUser);
    }

    @Test
    void param에_null이_입력되면_BusinessException_INVALID_INPUT_이_발생한다(){
        //given & when
        BusinessException exception = assertThrows(BusinessException.class,
                ()-> new PointFacadeDto.FindBalanceParam(null));

        // then
        assertEquals(ErrorCode.INVALID_INPUT,exception.getErrorStatus());
    }


    @Test
    void param에_존재하지_않은_사용자_아이디를_입력시_BusinessException_Repository_이_발생한다(){
        // given
        PointFacadeDto.FindBalanceParam param = new PointFacadeDto.FindBalanceParam(100L);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                ()-> pointFacade.findUserBalance(param));

        // then
        assertEquals(ErrorCode.Repository,exception.getErrorStatus());
    }

    @Test
    void param에_newUser_의_아이디를_넣으면_FindBalanceResult의_balance가_10000을_반환한다(){
        // given
        PointFacadeDto.FindBalanceParam param = new PointFacadeDto.FindBalanceParam(newUser.getId());

        //when
        PointFacadeDto.FindBalanceResult result = pointFacade.findUserBalance(param);

        // then
        assertEquals(
                newUser.getPoint(),
                result.balance(),
                "newUser 의 Point : "+newUser.getPoint()+" | result 의 balance 값 : "+result.balance());
    }




}