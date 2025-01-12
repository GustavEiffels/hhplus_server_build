package kr.hhplus.be.server.domain.concert;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository repository;

    /**
     * 콘서트가 존재하는지 확인
     * findById -> findConcert
     * @param concertId
     * @return
     */
    public Concert findConcert(Long concertId){
        return  repository.findById(concertId)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"콘서트가 존재하지 않습니다."));
    }
}
