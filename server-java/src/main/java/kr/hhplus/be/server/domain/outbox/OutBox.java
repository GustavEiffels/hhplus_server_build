package kr.hhplus.be.server.domain.outbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.*;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.config.Json.JsonUtils;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutBox extends BaseEntity {

    private OutBox(String eventType, String payload){
        this.eventType = eventType;
        this.payload   = payload  ;
        this.status    = OutBoxStatus.PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String eventType; // TOPIC NAME

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutBoxStatus status;

    // have to test
    public static OutBox create(String topicName, Object payloadObj){
        if(!hasText(topicName) || payloadObj == null ){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        try {
            String payload = JsonUtils.objectMapper().writeValueAsString(payloadObj);
            return new OutBox(topicName,payload);
        }
        catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_CONVERT_EXCEPTION);
        }
    }

    public static OutBox convertToOutBox(String payload){
        try {
            return JsonUtils.objectMapper().readValue(payload, OutBox.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getLocalizedMessage());
            throw new BusinessException(ErrorCode.JSON_CONVERT_EXCEPTION);
        }
    }

    public boolean isPending(){
        return this.status.equals(OutBoxStatus.PENDING);
    }

    public boolean isValid(){
        return super.getCreateAt() == null || !super.getCreateAt().isBefore(LocalDateTime.now().minusMinutes(1));
    }

    public void processed(){
        this.status = OutBoxStatus.PROCESSED;
    }

    public void failed(){
        this.status = OutBoxStatus.FAILED;
    }
}
