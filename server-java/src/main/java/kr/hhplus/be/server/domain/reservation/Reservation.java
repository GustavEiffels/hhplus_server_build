package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private ReservationStatus status = ReservationStatus.Reserved;

    @NotNull
    private Long amount;

    
    private LocalDateTime expiredAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;


    @ManyToOne(fetch = FetchType.LAZY  )
    @JoinColumn(
            name = "seat_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Seat seat;




    @Builder // 생성
    public Reservation(User user, Seat seat){
        if(user == null){
            throw new BusinessException(ErrorCode.Entity,"[사용자] 정보는 필수로 할당이 되어야 합니다.");
        }
        if(seat == null){
            throw new BusinessException(ErrorCode.Entity,"[좌석] 정보는 필수로 할당이 되어야 합니다.");
        }
        this.user = user;
        this.seat = seat;
        this.amount = seat.getPrice();
    }


    public void updateStatus(ReservationStatus status){
        if( status == null ){
            throw new BusinessException(ErrorCode.Entity,"[예약-상태] 변경 시 [예약-상태]는 필수 값 입니다.");
        }
        this.status = status;
    }

}
