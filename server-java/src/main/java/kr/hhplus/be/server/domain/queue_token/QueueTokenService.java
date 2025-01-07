package kr.hhplus.be.server.domain.queue_token;


import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueTokenService {
    private final QueueTokenRepository repository;

    /**
     * 대기열 토큰 생성
     * @param queueToken
     * @return
     */
    public QueueToken createToken(QueueToken queueToken){
        return repository.create(queueToken);
    }

    /**
     * 대기열 토큰이 유효하며, active 된 토큰인지 확인
     */
    public Boolean isValidAndActive(Long queueTokenId, Long userId){

        QueueToken queueToken = repository.findById(queueTokenId)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"잘못된 대기열 토큰입니다."));

        if(!Objects.equals(queueToken.getUser().getId(), userId)){
            throw new BusinessException(ErrorCode.Repository,"사용자가 대기열 토큰와 매칭되지 않습니다.");
        }

        if( queueToken.getExpireAt().isBefore(LocalDateTime.now()) ){
            throw new BusinessException(ErrorCode.Repository,"대기열이 만료된 토큰 입니다.");
        }

        return queueToken.getStatus().equals(QueueTokenStatus.Active);
    }
}
