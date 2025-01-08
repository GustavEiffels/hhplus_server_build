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
    private QueueTokenStatus status = QueueTokenStatus.Wait;

    private LocalDateTime expireAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public QueueToken(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.Entity,"[사용자] 정보는 필수로 입력이 되어야합니다.");
        }
        this.user = user;
    }

    // test-have to
    public void activate(){
        this.status   = QueueTokenStatus.Active;
        this.expireAt = LocalDateTime.now().plusMinutes(3);
    }
}
