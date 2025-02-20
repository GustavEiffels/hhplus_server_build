package kr.hhplus.be.server.application.outbox;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.event.ReservationEventPublisher;
import kr.hhplus.be.server.domain.event.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutBoxFacade {
    private final OutBoxService outBoxService;
    private final ReservationEventPublisher reservationEventPublisher;


    @Transactional
    public void reissuePendingEvent(){
        List<OutBox> outBoxList = outBoxService.findPending();

        if(!outBoxList.isEmpty()){
            outBoxList.forEach(item->{
                log.info("OUTBOX STATUS: {}",item.getStatus());
                log.info("OUTBOX EVENT TYPE: {}",item.getEventType());
                reservationEventPublisher.success(new ReservationSuccessEvent(item));
            });
        }
    }

    public void deleteExpireEvent(){
        outBoxService.deleteExpired();
    }
}
