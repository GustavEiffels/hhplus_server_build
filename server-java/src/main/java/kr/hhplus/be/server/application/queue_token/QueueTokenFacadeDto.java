package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.user.User;

public interface QueueTokenFacadeDto {

    record CreateQueueTokenCommand(User userEntity){

    }
}
