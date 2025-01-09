package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.schedule.ConcertScheduleRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.schedule.ScheduleJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

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
    ConcertFacade concertFacade;

    Concert concert0;
    Concert concert1;


    @BeforeEach
    void setUp_공연2개만들고_공연0은_예약가능_18_예약불가능_12_공연1은_예약가능_12_예약_불가능_18(){
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
    }

    @AfterEach
    void deleteAll(){
        concertJpaRepository.deleteAll();
        scheduleJpaRepository.deleteAll();
    }


// concnertId 로 콘서트 스케줄 정보 페이징해서 가져오기
    @Test
    void concert0의_아이디를_들고와서_page_1_로_해서_들고오면_스케줄_10개_들고오고_page_2_로_들고오면_8개_들고온다(){
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


}