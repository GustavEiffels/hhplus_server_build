package kr.hhplus.be.server.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    // CREATE
    public OutBox create(String topicNm, Object objectData){
        try {
            String payload = objectMapper.writeValueAsString(objectData);
            log.info("object DATA : {}",objectData);
            return outBoxRepository.create(OutBox.create(topicNm,payload));
        }
        catch (JsonProcessingException e){
            System.out.println(e.getLocalizedMessage());
            throw new BusinessException(ErrorCode.JSON_CONVERT_EXCEPTION);
        }
    }


}
