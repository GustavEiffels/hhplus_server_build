package kr.hhplus.be.server.application.point;


import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        User testUser = User.create("test");
        testUser.pointTransaction(10_000L);
        newUser = userJpaRepository.save(testUser);
    }


// 잔액 조회
    @DisplayName("포인트가 10,000이 있는 사용자의 id 값을 [param] 에 설정하여 findUserBalance 를 호출하면 잔액이 10,000 인 것을 확인할 수 있다.")
    @Test
    void findUserBalance(){
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
    @DisplayName("충전할 량을 20,000을 설정하고, 잔여 포인트가 10,000 인 사용자 아이디를 pointCharge 에 입력할때, 충전한 결과 30,000 원을 반환 받는다.")
    @Test
    void pointCharge(){
        // given
        PointFacadeDto.ChargeParam param = new PointFacadeDto.ChargeParam(newUser.getId(), 20_000L);

        // when
        PointFacadeDto.ChargeResult result = pointFacade.pointCharge(param);

        // then
        assertEquals(20_000L,result.amount(), "충전한 금액은 20,000");
        assertEquals( 30_000, userJpaRepository.findById(newUser.getId()).get().getPoint(),"충전한 사용자의 포인트 : 30,000");

    }
}