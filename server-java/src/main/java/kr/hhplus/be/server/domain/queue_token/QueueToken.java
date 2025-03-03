package kr.hhplus.be.server.domain.queue_token;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "queue_id")
    private Long id;

    @NotNull
    private QueueTokenStatus status = QueueTokenStatus.WAIT;

    private LocalDateTime expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;


    private QueueToken(User user){
        this.user = user;
    }

// METHOD
    public static QueueToken create(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        return new QueueToken(user);
    }

    public void activate(){
        this.status   = QueueTokenStatus.ACTIVE;
        this.expireAt = LocalDateTime.now().plusMinutes(10);
    }

    public void expire(){
        this.expireAt = LocalDateTime.now();
    }
}
