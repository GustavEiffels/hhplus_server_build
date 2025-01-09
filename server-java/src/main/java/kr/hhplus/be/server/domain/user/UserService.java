package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    /**
     * userid 로 User 를 조회
     * @param userid
     * @return
     * test - done
     */
    public User findById(Long userid){
        return userRepository.findById(userid)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"존재하지 않은 [사용자] 입니다."));
    }

    public User findByIdWithLock(Long userid){
        return userRepository.findByIdWithLock(userid)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"존재하지 않은 [사용자] 입니다."));

    }


}
