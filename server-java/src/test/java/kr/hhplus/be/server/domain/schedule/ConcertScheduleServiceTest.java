package kr.hhplus.be.server.domain.schedule;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertScheduleServiceTest {


    @Mock
    ConcertScheduleRepository scheduleRepository;

    @InjectMocks
    ConcertScheduleService concertScheduleService;

    private Long concertId;
    private Concert mockConcert;
    private ConcertSchedule concertSchedule;
    @BeforeEach
    void setUp() {
        concertId = 1L;
        mockConcert = Mockito.mock(Concert.class);
    }

    private ConcertSchedule createConcertSchedule(LocalDateTime reserveStartTime, LocalDateTime reserveEndTime, boolean isReservationEnabled) {
        ConcertSchedule schedule = ConcertSchedule.builder()
                .concert(mockConcert)
                .reserveStartTime(reserveStartTime)
                .reserveEndTime(reserveEndTime)
                .showTime(LocalDateTime.now().plusDays(2))
                .build();
        if (!isReservationEnabled) {
            schedule.disableReservation();
        }
        return schedule;
    }

    @DisplayName("존재하지 않은 콘서트 스케줄을 입력 받으면 [ ErrorCode : NOT_FOUND_CONCERT_SCHEDULE ] 가 발생한다.")
    @Test
    void concertScheduleService_findReservableConcertSchedule_Test_00() {
        // given
        when(scheduleRepository.findById(concertId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> concertScheduleService.findReservableConcertSchedule(concertId));

        // then
        assertEquals(ErrorCode.NOT_FOUND_CONCERT_SCHEDULE, exception.getErrorStatus());
    }

    @DisplayName("반환 받은 스케줄이 예약 가능한 상태가 아니라면 [ ErrorCode : RESERVATION_END ] 가 발생한다.")
    @Test
    void concertScheduleService_findReservableConcertSchedule_Test_01() {
        // given
        concertSchedule = createConcertSchedule(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(3),
                false);

        when(scheduleRepository.findById(concertId)).thenReturn(Optional.of(concertSchedule));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> concertScheduleService.findReservableConcertSchedule(concertId));

        // then
        assertEquals(ErrorCode.RESERVATION_END, exception.getErrorStatus());
    }

    @DisplayName("조회한 콘서트 스케줄이 예약가능한 시간대가 아닌 경우 ErrorCode : NOT_RESERVABLE_TIME 가 발생한다.")
    @Test
    void concertScheduleService_findReservableConcertSchedule_Test_02() {
        // given
        concertSchedule = createConcertSchedule(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                true);

        when(scheduleRepository.findById(concertId)).thenReturn(Optional.of(concertSchedule));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> concertScheduleService.findReservableConcertSchedule(concertId));

        // then
        assertEquals(ErrorCode.NOT_RESERVABLE_TIME, exception.getErrorStatus());
    }

}