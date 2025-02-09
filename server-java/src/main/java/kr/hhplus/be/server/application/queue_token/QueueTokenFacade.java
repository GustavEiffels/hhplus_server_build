package kr.hhplus.be.server.application.queue_token;


public interface QueueTokenFacade {
    QueueTokenFacadeDto.CreateResult create(QueueTokenFacadeDto.CreateParam param);

    QueueTokenFacadeDto.ActiveCheckResult isValidToken(QueueTokenFacadeDto.ActiveCheckParam param);

    void activate(QueueTokenFacadeDto.ActivateParam activateParam);



    void expire();
}
