package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    @DisplayName("예약 된 좌석 아이디 리스트를 reservableAndReturnSchedules 에 입력 하면, 해당 좌석은 예약 가능한 상태로 변경이 된다.")
    @Test
    void reservableAndReturnSchedules_Test00(){
        // when
        List<Long> seatIds = new ArrayList<>();
        Concert concert = mock(Concert.class);
        LocalDateTime first = LocalDateTime.now().plusDays(1);
        LocalDateTime second = LocalDateTime.now().plusDays(2);
        LocalDateTime third = LocalDateTime.now().plusDays(3);
            // 스케줄 1 & 좌석 1
        ConcertSchedule concertSchedule1 = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(first)
                .reserveEndTime(second)
                .showTime(third)
                .build();
        Seat seat1 = Seat.builder()
                .concertSchedule(concertSchedule1)
                .price(10_000L)
                .seatNo(1)
                .build();
        seat1.reserve();
            // 스케줄 2 & 좌석 2
        ConcertSchedule concertSchedule2 = ConcertSchedule.builder()
                .concert(concert)
                .reserveStartTime(first)
                .reserveEndTime(second)
                .showTime(third)
                .build();
        Seat seat2 = Seat.builder()
                .concertSchedule(concertSchedule2)
                .price(20_000L)
                .seatNo(2)
                .build();
        seat2.reserve();

        assertEquals(SeatStatus.RESERVED,seat1.getStatus());
        assertEquals(SeatStatus.RESERVED,seat2.getStatus());

        List<Seat> seatList = new ArrayList<>();
        seatList.add(seat1);
        seatList.add(seat2);


        when(seatRepository.findByIdsWithLock(seatIds)).thenReturn(seatList);

        // when
        List<Long> scheduleIds = seatService.reservableAndReturnSchedules(seatIds);

        // then
        assertEquals(SeatStatus.RESERVABLE,seat1.getStatus());
        assertEquals(SeatStatus.RESERVABLE,seat2.getStatus());
        assertEquals(2,scheduleIds.size());



    }
}