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
     * User 를 단순 조회 목적으로 조회 시, User 를 조회
     * - Lock 을 사용하지 않음
     * @param userid
     * @return
     * test - done
     */
    public User findUser(Long userid){
        return userRepository.findById(userid)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"존재하지 않은 [사용자] 입니다."));
    }


    /**
     * User 를 조회하고 조회한 User 의 포인트를 충전한다.
     * - 포인트 충전을 명시적으로 수행하기 위해 작성
     * - Domain Service 에서 도메인에 대한 비지니스 로직을 수행하기 위해서
     * @param userId
     * @param amount
     * @return
     */
    public User chargePoints(Long userId, Long amount){
        User user = findUserForUpdate(userId);
        user.pointTransaction(amount);
        return user;
    }

    /**
     * 사용자 정보를 수정할 목적으로 조회 시, User 를 조회
     * - 비관적 락을 사용
     * @param userId
     * @return
     */
    public User findUserForUpdate(Long userId){
        return userRepository.findByIdWithLock(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"존재하지 않은 [사용자] 입니다."));
    }

    public User findByIdWithLock(Long userid){
        return userRepository.findByIdWithLock(userid)
                .orElseThrow(()-> new BusinessException(ErrorCode.Repository,"존재하지 않은 [사용자] 입니다."));

    }


}
