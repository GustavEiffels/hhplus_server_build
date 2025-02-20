package kr.hhplus.be.server.application.outbox;

import kr.hhplus.be.server.domain.event.ReservationEventPublisher;
import kr.hhplus.be.server.domain.event.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutBoxFacade {
    private final OutBoxService outBoxService;
    private final ReservationEventPublisher reservationEventPublisher;


    public void reissuePendingEvent(){
        List<OutBox> outBoxList = outBoxService.findPending();
        if(!outBoxList.isEmpty()){
            outBoxList.forEach(item->{
                reservationEventPublisher.success(new ReservationSuccessEvent(item));
            });
        }
    }

    public void deleteExpireEvent(){
        outBoxService.deleteExpired();
    }
}
