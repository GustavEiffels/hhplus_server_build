package kr.hhplus.be.server.domain.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    public Reservation create(Reservation reservation){
        return repository.save(reservation);
    }


    public List<Reservation> findExpiredWithLock(){
        return null;
    }

}
