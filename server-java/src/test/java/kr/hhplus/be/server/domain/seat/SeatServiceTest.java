package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {
    @Mock
    SeatRepository seatRepository;

    @InjectMocks
    SeatService seatService;

// findReservableForUpdate
    @DisplayName("좌석 리스트 아이디로 조회 시, 존재하는 좌석이 없다면 [ ErrorCode : NOT_FOUND_SEAT ] 발생한다. ")
    @Test
    void findReservableForUpdate_Test00(){
        // given
        List<Long> seatIds = new ArrayList<>();
        List<Seat> seats   = new ArrayList<>();
        when(seatRepository.findByIdsWithLock(seatIds)).thenReturn(seats);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> seatService.findReservableForUpdate(seatIds));

        // then
        assertEquals(ErrorCode.NOT_FOUND_SEAT,exception.getErrorStatus());
    }

    @DisplayName("좌석 리스트 아이디로 조회 시, 예약이 된 좌석이 있다면 [ ErrorCode : NOT_RESERVABLE_DETECTED ] 발생한다. ")
    @Test
    void findReservableForUpdate_Test01(){
        // given
        List<Long> seatIds = new ArrayList<>();
        List<Seat> seats   = new ArrayList<>();
        Seat seat1 = mock(Seat.class);
        Seat seat2 = mock(Seat.class);
        seats.add(seat1);
        seats.add(seat2);
        when(seatRepository.findByIdsWithLock(seatIds)).thenReturn(seats);
        when(seat1.getStatus()).thenReturn(SeatStatus.RESERVED);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> seatService.findReservableForUpdate(seatIds));

        // then
        assertEquals(ErrorCode.NOT_RESERVABLE_DETECTED,exception.getErrorStatus());
    }
}