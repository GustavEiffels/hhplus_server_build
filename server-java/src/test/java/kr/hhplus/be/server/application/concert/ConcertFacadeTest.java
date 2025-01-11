package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.seat.SeatJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ConcertFacade concertFacade;

    Concert concert0;
    Concert concert1;

    ConcertSchedule schedule0;


    @BeforeEach
    void setUp_공연0은예약가능_18_예약불가능_12_공연1_예약가능_12_예약_불가능_18_스케줄0_좌석50개_짝수_점유_홀수_예약가능(){
        concert0 = Concert.builder().title("서커스0").performer("김광대").build();
        concertJpaRepository.save(concert0);

        concert1 = Concert.builder().title("서커스1").performer("이광대").build();
        concertJpaRepository.save(concert1);

        List<ConcertSchedule> concertScheduleList0 = new ArrayList<>();
        List<ConcertSchedule> concertScheduleList1 = new ArrayList<>();

        for(int i = 1; i<=30; i++){
            if(i < 19){
                concertScheduleList0.add(
                        ConcertSchedule.builder().concert(concert0)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().minusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
                concertScheduleList1.add(
                        ConcertSchedule.builder().concert(concert1)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().plusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
            }
            else{
                concertScheduleList1.add(
                        ConcertSchedule.builder().concert(concert1)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().minusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
                concertScheduleList0.add(
                        ConcertSchedule.builder().concert(concert0)
                                .showTime(LocalDateTime.now().plusDays(30))
                                .reserveStartTime(LocalDateTime.now().plusHours(3))
                                .reserveEndTime(LocalDateTime.now().plusHours(5))
                                .build());
            }

        }

        scheduleJpaRepository.saveAll(concertScheduleList0);
        scheduleJpaRepository.saveAll(concertScheduleList1);

        schedule0 = concertScheduleList0.get(0);

        List<Seat> seatList = new ArrayList<>();
        for(int i = 1; i<=50; i++){
            Seat newSeat = Seat.builder().seatNo(i).concertSchedule(schedule0)
                    .price(300_000L).build();

            if(i % 2 == 0){
                newSeat.updateStatus(SeatStatus.OCCUPIED);
            }
            seatList.add(newSeat);
        }
        seatJpaRepository.saveAll(seatList);
    }

    @AfterEach
    void deleteAll(){
        concertJpaRepository.deleteAll();
        scheduleJpaRepository.deleteAll();
    }


// concnertId 로 콘서트 스케줄 정보 페이징해서 가져오기
    @Test
    void concert0의_아이디를_들고와서_page1_10_page2_8_concert1_page1_10_page2_2(){
        // given
        Long concertId0 = concert0.getId();
        Long concertId1 = concert1.getId();
        ConcertFacadeDto.FindScheduleParam param0_1 = new ConcertFacadeDto.FindScheduleParam(concertId0,1);
        ConcertFacadeDto.FindScheduleParam param0_2 = new ConcertFacadeDto.FindScheduleParam(concertId0,2);
        ConcertFacadeDto.FindScheduleParam param1_1 = new ConcertFacadeDto.FindScheduleParam(concertId0,1);
        ConcertFacadeDto.FindScheduleParam param1_2 = new ConcertFacadeDto.FindScheduleParam(concertId1,2);

        // when
        ConcertFacadeDto.FindScheduleResult result0_0 = concertFacade.findSchedules(param0_1);
        ConcertFacadeDto.FindScheduleResult result0_1 = concertFacade.findSchedules(param0_2);
        ConcertFacadeDto.FindScheduleResult result1_0 = concertFacade.findSchedules(param1_1);
        ConcertFacadeDto.FindScheduleResult result1_1 = concertFacade.findSchedules(param1_2);

        // then
        assertEquals(10,result0_0.scheduleInfoList().size()); // paging 해서 10 : 1 패이지
        assertEquals(8,result0_1.scheduleInfoList().size());  // paging 해서 8  : 2 패이지
        assertEquals(concert0.getTitle(),result0_1.title());
        assertEquals(10,result1_0.scheduleInfoList().size()); // paging 해서 10 : 1 패이지
        assertEquals(2,result1_1.scheduleInfoList().size());  // paging 해서 2  : 2 패이지
        assertEquals(concert1.getPerformer(),result1_1.performer());
    }

// 예약 가능한 좌석 조회 API

    @Test
    void 스케줄_아이디_입력하면_예약가능한_좌석수_25개_표출되어야하며_좌석_번호들은_모두_홀수이고_가격은_300_000원이다(){
        // given :
        ConcertFacadeDto.FindLeftSeatParam param = new ConcertFacadeDto.FindLeftSeatParam(schedule0.getId());

        // when
        ConcertFacadeDto.FindLeftSeatResult result = concertFacade.findAvailableSeats(param);

        // then
        assertEquals(25,result.seatInfoList().size());
        for(ConcertFacadeDto.SeatInfo info : result.seatInfoList()){
            if(info.seatNo()%2 == 0){
                fail();
            }
            if(info.price() != 300_000L){
                fail();
            }
        }
    }

}