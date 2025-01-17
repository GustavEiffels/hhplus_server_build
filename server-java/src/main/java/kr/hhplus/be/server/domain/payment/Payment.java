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

    private Payment(Long amount, Reservation reservation){
        this.amount          = amount;
        this.reservation     = reservation;
        this.paymentStatus   = PaymentStatus.SUCCESS;
    }

    public static Payment create(Long amount, Reservation reservation){
        if( amount < 0){
            throw new BusinessException(ErrorCode.NOT_VALID_PAYMENT_AMOUNT);
        }
        if( reservation == null ){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        return  new Payment(amount,reservation);
    }

    public void cancel(){
        this.paymentStatus = PaymentStatus.CANCEL;
    }

}
