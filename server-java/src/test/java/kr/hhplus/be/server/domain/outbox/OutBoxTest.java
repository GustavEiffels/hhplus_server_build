package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@DisplayName("""
        OUTBOX - DOMAIN TEST 
        """)
class OutBoxTest {

    @DisplayName("""
            OutBox 생성 시, Topic 이름을 설정하지 않으면  BusinessException 예외가 발생한다.
            """)
    @Test
    void create_test_00(){
        // given & when
        String testObjectData = "null";
        BusinessException exception = assertThrows(
                BusinessException.class,()-> OutBox.create(null,testObjectData));

        // then
        assertEquals(exception.getErrorStatus(), ErrorCode.REQUIRE_FIELD_MISSING);
    }

    @DisplayName("""
            OutBox 생성 시, 변환할 객체를 할당하지 않으면 BusinessException 예외가 발생한다.
            """)
    @Test
    void create_test_01(){
        // given & when
        String topicName      = "test";
        BusinessException exception = assertThrows(
                BusinessException.class,()-> OutBox.create(topicName,null));

        // then
        assertEquals(exception.getErrorStatus(), ErrorCode.REQUIRE_FIELD_MISSING);
    }

    @DisplayName("""
            OutBox 생성 시, 정상 작동하는 경우 OutBox 객체의 Status 는 Pending 이다. 
            """)
    @Test
    void create_test_02() {
        // given
        String       topicName      = "test";
        String       testObjectData = "null";
        OutBox       outBox         = new OutBox(topicName,testObjectData);


        // when & then
        assertEquals(OutBoxStatus.PENDING, outBox.getStatus());
    }
}