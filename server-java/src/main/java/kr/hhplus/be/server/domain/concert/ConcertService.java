package kr.hhplus.be.server.domain.concert;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository repository;

    // 존재하는 콘서트인지 확인
    public Concert findById(Long concertId){
        return  repository.findById(concertId).orElseThrow(()-> new BusinessException(ErrorCode.Repository,"콘서트가 존재하지 않습니다."));
    }
}
