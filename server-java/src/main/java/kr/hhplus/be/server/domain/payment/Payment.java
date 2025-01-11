package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long id;


    @NotNull
    @Min(0)
    private Long amount;

    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reservation_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    @Builder // 초기 생성
    public Payment(Long amount, Reservation reservation){


        if( amount < 0){
            throw new BusinessException(ErrorCode.Entity,"결제 금액은 0보다 커야합니다.");
        }

        if( reservation == null ){
            throw new BusinessException(ErrorCode.Entity,"[예약] 정보는 필수 값 입니다.");
        }

        this.amount          = amount;
        this.reservation     = reservation;
        this.paymentStatus   = PaymentStatus.Success;
    }

    public void cancel(){
        this.paymentStatus = PaymentStatus.Cancel;
    }

}
