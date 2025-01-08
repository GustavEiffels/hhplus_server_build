package kr.hhplus.be.server.domain.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    public Reservation create(Reservation reservation){
        return repository.save(reservation);
    }

}
