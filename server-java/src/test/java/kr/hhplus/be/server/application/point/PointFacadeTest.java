package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.point_history.PointHistory;
import kr.hhplus.be.server.domain.point_history.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.point_history.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointFacadeTest {

    @Autowired
    PointHistoryJpaRepository pointHistoryJpaRepository;

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


// 잔액 조회
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



// 잔액 충전
    @Test
    void param에_충전포인트를_20_000을넣으면_result의_amount_로_20_000이되고_user의_point는_30_000이_된다(){
        // given
        PointFacadeDto.ChargeParam param = new PointFacadeDto.ChargeParam(newUser.getId(), 20_000L);

        // when
        PointFacadeDto.ChargeResult result = pointFacade.pointCharge(param);

        // then
        assertEquals(20_000L,result.amount(), "충전한 금액은 20,000");
        assertEquals( 30_000, userJpaRepository.findById(newUser.getId()).get().getPoint(),"충전한 사용자의 포인트 : 30,000");

    }
}