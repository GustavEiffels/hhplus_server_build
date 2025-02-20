package kr.hhplus.be.server.domain.platform;


import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformService {

    public OutBox getEventFromReservationCreate(String eventString){

        OutBox outBox = OutBox.convertToOutBox(eventString);

        if( outBox.isPending() ) {
            log.info("이벤트 플렛 폼에서 처음으로 해당 이벤트가 처리 됩니다.");
            if( outBox.isValid() ){
                outBox.processed();
                log.info("이벤트가 유효하여 정상적인 로직 수행");
            }
            else{
                outBox.failed();
                log.info("유효하지 않은 이벤트라 실패 처리");
            }
        }
        return outBox;
    }


}
