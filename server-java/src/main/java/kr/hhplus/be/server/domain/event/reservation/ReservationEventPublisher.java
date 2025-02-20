package kr.hhplus.be.server.domain.event.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(ReservationSuccessEvent event){
        applicationEventPublisher.publishEvent(event);
    }

}
