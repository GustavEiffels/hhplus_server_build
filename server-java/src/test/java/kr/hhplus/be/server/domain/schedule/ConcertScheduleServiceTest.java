package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.ConcertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertScheduleServiceTest {

    @Mock
    ConcertRepository repository;

    @InjectMocks
    ConcertService concertService;

    @Test
    void 유효하지_않은_콘서트_아이디를_입력시_BusinessException_Repository_예외가_발생한다(){
        //given
        Long concertId = 1L;
        when(repository.findById(concertId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            concertService.findConcert(concertId);
        });
        assertEquals(ErrorCode.Repository, exception.getErrorStatus());
    }

}