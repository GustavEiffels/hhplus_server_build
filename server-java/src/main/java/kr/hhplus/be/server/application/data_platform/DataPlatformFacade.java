package kr.hhplus.be.server.application.data_platform;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.data_platform.DataPlatformService;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPlatformFacade {

    private final OutBoxService outBoxService;
    private final DataPlatformService dataPlatformService;

    @Transactional
    public boolean getEventFromReservationCreate(String key, String payload) {

        log.info("DataPlatformFacade - key : {}", key);
        log.info("DataPlatformFacade - payload : {}", payload);

        Long outBoxId = Long.valueOf(key);
        OutBox outBox = outBoxService.findOutbox(outBoxId);

        // OutBox 상태 확인
        if (!outBox.isPending()) {
            return false;
        }

        // OutBox 유효성 검사
        boolean isValid = outBox.isValid();
        log.info("OutBox is {}", isValid ? "valid" : "invalid");

        if (isValid) {
            log.info("정상적으로 로직 작동");
            dataPlatformService.dataPlatformLogic();
            outBoxService.updatePROCESSED(outBoxId);
        } else {
            outBoxService.updateFAILED(outBoxId);
        }

        return isValid;
    }
}
