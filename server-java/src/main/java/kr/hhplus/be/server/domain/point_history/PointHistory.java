package kr.hhplus.be.server.domain.point_history;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Enabled;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private long amount;

    @NotNull
    private PointHistoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_id",
            //nullable = false, => 충전 시 payment 는 존재하지 않음
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Payment payment;


    private PointHistory(long amount, User user, Payment payment, PointHistoryStatus status) {
        this.amount = amount;
        this.user = user;
        this.payment = payment;
        this.status = status;
    }

    public static PointHistory createCharge(Long amount,User user) {
        validateAmount(amount);
        validateUser(user);
        return new PointHistory(amount, user, null, PointHistoryStatus.CHARGE);
    }

    public static PointHistory createUse(Long amount, User user, Payment payment) {
        validateAmount(amount);
        validateUser(user);
        validatePayment(payment);
        return new PointHistory(amount, user, payment, PointHistoryStatus.USE);
    }

    private static void validateAmount(Long amount) {
        if (amount < 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
    }

    private static void validatePayment(Payment payment) {
        if (payment == null) {
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
    }
}
