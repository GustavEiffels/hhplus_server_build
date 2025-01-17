package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcertTest {

    @DisplayName("콘서트 제목이 존재하지 않으면 ErrorCode : REQUIRE_FIELD_MISSING 발생")
    @Test
    void concert_constructor_test00(){
        // given
        String title = "";
        String performer = "김가수";

        // when
        BusinessException be = assertThrows(
                BusinessException.class, () -> Concert.create(title,performer) );


        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,be.getErrorStatus());
    }


    @DisplayName("콘서트 공연자가 설정되지 않으면 ErrorCode : REQUIRE_FIELD_MISSING 발생")
    @Test
    void concert_constructor_test01(){
        // given
        String title = "서커스!";
        String performer = "";

        // when
        BusinessException be = assertThrows(
                BusinessException.class, () -> Concert.create(title,performer) );

        // then
        assertEquals(ErrorCode.REQUIRE_FIELD_MISSING,be.getErrorStatus());
    }

    @DisplayName("콘서트 생성 시, 공연 이름과 공연자를 입력하면, Concert 객체가 성공적으로 만들어진다.")
    @Test
    void concert_constructor_test02(){
        // given
        String title = "서커스!";
        String performer = "김광대";

        // when
        Concert concert = Concert.create(title,performer);

    }


}