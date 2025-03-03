package kr.hhplus.be.server.domain.reservation;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private ReservationStatus status = ReservationStatus.PENDING;

    @NotNull
    private Long amount;

    private LocalDateTime expiredAt;

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private User user;


    @ManyToOne(fetch = FetchType.LAZY  )
    @JoinColumn(
            name = "seat_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Seat seat;


    private Reservation(User user, Seat seat){
        if(user == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if(seat == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        this.user      = user;
        this.seat      = seat;
        this.amount    = seat.getPrice();
        this.expiredAt = LocalDateTime.now().plusMinutes(10);
    }

    public static Reservation create(User user, Seat seat){
        if(user == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if(seat == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        return new Reservation(user,seat);
    }

    public void expired(){
        this.status    = ReservationStatus.EXPIRED;
        this.expiredAt = LocalDateTime.now();
    }

    public void reserve(){
        this.status    = ReservationStatus.RESERVED;
        this.expiredAt = LocalDateTime.now();
    }

    /**
     * @param userId
     */
    public void isReserveUser(Long userId){
        if(!this.user.getId().equals(userId)){
            throw new BusinessException(ErrorCode.NOT_RESERVATION_OWNER);
        }
    }

    /**
     * 만료된 예약 인지 확인
     */
    public void isExpired(){
        if(this.expiredAt.isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.EXPIRE_RESERVATION);
        }
    }
}
