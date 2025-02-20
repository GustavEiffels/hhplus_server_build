package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.common.config.Json.JsonUtils;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutBoxService {
    private final OutBoxRepository outBoxRepository;

    // CREATE
    public OutBox create(OutBox outBox){
        return outBoxRepository.create(outBox);
    }

    // FIND
    public OutBox findOutbox(Long outboxId){
        return outBoxRepository.findById(outboxId)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_OUTBOX));
    }

    // DELETE
    public void deleteExpired(){
        List<OutBox> deleteOutboxList = outBoxRepository.findDeleteList();
        if(!deleteOutboxList.isEmpty()){
            outBoxRepository.deleteAll(deleteOutboxList);
        }
    }


    public List<OutBox> findPending(){
        return outBoxRepository.findPendingByStatus();
    }

    public List<OutBox> findDeleteList(){ return ; }


    public OutBox updatePROCESSED(Long outboxId){
        OutBox outBox = findOutbox(outboxId);
        outBox.processed();
        log.info("id : {} - status : {}",outboxId,outBox.getStatus());
        return outBoxRepository.update(outBox);
    }

    public OutBox updateFAILED(Long outboxId){
        OutBox outBox = findOutbox(outboxId);
        outBox.failed();
        log.info("id : {} - status : {}",outboxId,outBox.getStatus());
        return outBoxRepository.update(outBox);
    }

}
