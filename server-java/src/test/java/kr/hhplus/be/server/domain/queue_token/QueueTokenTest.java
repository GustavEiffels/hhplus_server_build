package kr.hhplus.be.server.domain.queue_token;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.yaml.snakeyaml.tokens.Token;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QueueTokenTest {

    @DisplayName("대기열 토큰 생성 시, 사용자 정보를 입력하지 않으면 ErrorCode : REQUIRE_FIELD_MISSING 가 발생한다.")
    @Test
    void  createToken_00(){
        // given & when
        BusinessException exception = assertThrows(BusinessException.class, ()->QueueToken.create(null));


        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,exception.getErrorStatus());
    }

    @DisplayName("대기열 토큰 생성 하고 나서, activate 를 시키면 토큰의 Status 가 Active 로 변경되고 expireAt 에 시간이 할당 된다.")
    @Test
    void activate_Test_00(){
        // given
        User user = User.create("test");
        QueueToken queueToken = QueueToken.create(user);
        QueueTokenStatus beforeStatus = queueToken.getStatus();
        assertEquals(QueueTokenStatus.WAIT,beforeStatus);

        // when
        queueToken.activate();

        // then
        assertEquals(QueueTokenStatus.ACTIVE,queueToken.getStatus(),"토큰 상태 값 \"ACTIVE\" ");
        assertNotNull(queueToken.getExpireAt(),"토큰 만료 시간 할당");
    }

    @DisplayName("대기열 토큰 활성화 후 만료 가 되면, 토큰 만료 시간은 현재 시간 보다 과거 시간이 된다.")
    @Test
    void expire_Test_00(){
        // given
        User user = User.create("test");
        QueueToken queueToken = QueueToken.create(user);
        queueToken.activate();

        // when
        queueToken.expire();

        // then
        assertTrue(queueToken.getExpireAt().isBefore(LocalDateTime.now()));
    }

}