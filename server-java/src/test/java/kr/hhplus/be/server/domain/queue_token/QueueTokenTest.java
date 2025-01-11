package kr.hhplus.be.server.domain.queue_token;

import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueTokenTest {

    @Test
    void  대기열_토큰_activate하면_token의_status_가_activate_가_되고_expireAt_에_시간이_할당된다(){
        // given
        User testUser = User.builder().name("김연습").build();
        QueueToken queueToken = QueueToken.builder().user(testUser).build();

        // when
        queueToken.activate();

        assertEquals(QueueTokenStatus.Active,queueToken.getStatus(),"토큰이 활성화 되면 status 값이 Active 가 됨");
        assertNotNull(queueToken.getExpireAt(),"activate 되면 만료시간이 생성됨.");
    }

}