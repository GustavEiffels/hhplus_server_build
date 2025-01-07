package kr.hhplus.be.server.domain.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository repository;

    // lock 을 거는지
    public List<ConcertSchedule> findReservableScheduleList(Long concertId, int page){
        return repository.findReservableScheduleList(concertId,page);
    }

}
