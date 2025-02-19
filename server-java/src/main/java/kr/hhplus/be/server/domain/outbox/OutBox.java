package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static org.springframework.util.StringUtils.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutBox extends BaseEntity {

    public OutBox(String eventType, String payload){
        this.eventType = eventType;
        this.payload   = payload  ;
        this.status    = OutBoxStatus.PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String eventType; // TOPIC NAME

    private String payload;

    @Enumerated(EnumType.STRING)
    private OutBoxStatus status;

    // have to test
    public static OutBox create(String topicName, String payload){
        if(!hasText(topicName) || !hasText(payload)){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        return new OutBox(topicName,payload);
    }
}
