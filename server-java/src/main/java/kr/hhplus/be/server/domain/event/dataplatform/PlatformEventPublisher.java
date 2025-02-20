package kr.hhplus.be.server.domain.event.dataplatform;

import kr.hhplus.be.server.domain.event.reservation.ReservationSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class PlatformEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(PlatformOutBoxUpdateEvent event){
        applicationEventPublisher.publishEvent(event);
    }
}
