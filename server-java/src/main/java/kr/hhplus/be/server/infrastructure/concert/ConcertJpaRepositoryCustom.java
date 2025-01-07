package kr.hhplus.be.server.infrastructure.concert;


import kr.hhplus.be.server.domain.concert.Concert;

import java.util.Optional;

public interface ConcertJpaRepositoryCustom  {

    Optional<Concert> findByConcertId(Long concertId);
}
