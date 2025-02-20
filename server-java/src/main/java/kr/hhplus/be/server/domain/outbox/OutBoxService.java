package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.common.config.Json.JsonUtils;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


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



    public OutBox updateFromConsumer(String consumerPayload){
        Long outBoxId = OutBox.convertToOutBox(consumerPayload).getId();
        OutBox outBox = findOutbox(outBoxId);
        return outBoxRepository.create(outBox);
    }

}
