package kr.hhplus.be.server.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutBoxService {
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;

    public OutBox create(String topicNm, Object objectData){
        try {
            String payload = objectMapper.writeValueAsString(objectData);
            return outBoxRepository.create(OutBox.create(topicNm,payload));
        }
        catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_CONVERT_EXCEPTION);
        }
    }

}
