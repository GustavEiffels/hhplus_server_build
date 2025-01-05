package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcertTest {

    @Test
    void 콘서트에_콘서트_제목이_존재하지_않으면_BaseException_예외가_발생(){
        // given
        String title = "";
        String performer = "김가수";

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    Concert.builder().title(title).performer(performer).build();
                });

        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }

    @Test
    void 콘서트에_콘서트_공연자가_존재하지_않으면_BaseException_예외가_발생(){
        // given
        String title = "서커스!";
        String performer = "";

        // when
        BusinessException be = assertThrows(
                BusinessException.class,()->{
                    Concert.builder().title(title).performer(performer).build();
                });

        // then
        assertEquals(ErrorCode.Entity,be.getErrorStatus());
    }

}