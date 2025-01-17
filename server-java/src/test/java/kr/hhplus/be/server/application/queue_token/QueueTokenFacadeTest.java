package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import kr.hhplus.be.server.domain.queue_token.QueueTokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueueTokenFacadeTest {


    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    TokenJpaRepository tokenJpaRepository;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    List<User> userList;

    @BeforeEach
    void setUp(){
        List<User> newUserList = userList = new ArrayList<>();
        for(int i = 0; i<10; i++){
            newUserList.add(User.create("바보"+i));
        }
        userList = userJpaRepository.saveAll(newUserList);
    }

    @AfterEach
    void tearDown() {
        tokenJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

//  유저 대기열 토큰 발급 받는 API
    @Test
    void 유저10명이_토큰발급_요청을하면_10개의토큰이_생성된다(){
        //given
        List<QueueTokenFacadeDto.CreateParam> paramList = new ArrayList<>();
        userList.forEach(item->{
            paramList.add(new QueueTokenFacadeDto.CreateParam(item.getId()));
        });

        // when
        paramList.forEach(item->{
            queueTokenFacade.create(item);
        });

        //then
        assertEquals(10,tokenJpaRepository.count());
    }


// 인터셉터에서 토큰이 활성화가 되었는지 확인
    @Test
    void 활성화_상태인_토큰과_해당토큰_사용자_아이디를_넣으면_true_값을_반환한다(){
        // given
        User user   = userList.get(0);
        QueueToken token = tokenJpaRepository.save(QueueToken.create(user));
        token.activate();
        tokenJpaRepository.save(token);

        QueueTokenFacadeDto.ActiveCheckParam param = new QueueTokenFacadeDto.ActiveCheckParam(String.valueOf(token.getId()),String.valueOf(user.getId()));

        // when
        QueueTokenFacadeDto.ActiveCheckResult result = queueTokenFacade.isValidToken(param);

        // then
        assertTrue(result.isActive());
    }

//  최대 활성화 가능 토큰 수까지 토큰 활성화
    @Test
    void 최대_활성화_토큰수가_10이고_현재_활성화된_토큰수가_3개이며_토큰의수가_20개일때_활성화된_토큰의수가_10개가_되고_비활성화_토큰의수가_20개가_된다(){
        // given
        User testUser = userList.get(0);
            // createToken - 30
            List<QueueToken> queueTokens = new ArrayList<>();
            for(int i = 1; i<=30;i++){
                QueueToken queueToken = QueueToken.create(testUser);
                if(i%10==0){
                    queueToken.activate();
                }
                queueTokens.add(queueToken);
            }
            tokenJpaRepository.saveAll(queueTokens);

        int activeCnt = 0;
        int waitCnt   = 0;
        List<QueueToken> tokenBefore = tokenJpaRepository.findAll();
        for (QueueToken item : tokenBefore) {
            if (item.getStatus() == QueueTokenStatus.ACTIVE) {
                activeCnt++;
            } else if (item.getStatus() == QueueTokenStatus.WAIT) {
                waitCnt++;
            }
        }
        assertEquals(3,activeCnt);
        assertEquals(27,waitCnt);

        // When
        queueTokenFacade.activate(new QueueTokenFacadeDto.ActivateParam(10L));

        // Then
        activeCnt = 0;
        waitCnt   = 0;
        List<QueueToken> tokenAfter = tokenJpaRepository.findAll();
        for (QueueToken item : tokenAfter) {
            if (item.getStatus() == QueueTokenStatus.ACTIVE) {
                activeCnt++;
            } else if (item.getStatus() == QueueTokenStatus.WAIT) {
                waitCnt++;
            }
        }

        assertEquals(10,activeCnt);
        assertEquals(20,waitCnt);
    }


}