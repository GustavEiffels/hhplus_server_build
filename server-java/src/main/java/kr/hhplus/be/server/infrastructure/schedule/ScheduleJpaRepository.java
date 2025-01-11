package kr.hhplus.be.server.infrastructure.schedule;

import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long>, ScheduleJpaRepositoryCustom {

}
