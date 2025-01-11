package kr.hhplus.be.server.domain.concert;

import java.util.Optional;

public interface ConcertRepository {

    Optional<Concert> findById(Long concertId);
}
