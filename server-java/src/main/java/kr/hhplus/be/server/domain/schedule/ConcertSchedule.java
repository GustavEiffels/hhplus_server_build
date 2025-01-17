package kr.hhplus.be.server.domain.schedule;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id")
    private Long id;

    @NotNull
    private LocalDateTime showTime;

    @NotNull
    private LocalDateTime reservation_start;

    @NotNull
    private LocalDateTime reservation_end;

    @NotNull
    private Boolean       isReserveAble = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id",nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Concert concert;


    @Builder
    public ConcertSchedule(
            LocalDateTime showTime,
            LocalDateTime reserveStartTime,
            LocalDateTime reserveEndTime,
            Concert concert){
        if(concert == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        reschedule(showTime,reserveStartTime,reserveEndTime);
        this.concert = concert;
    }

    public void reschedule(
            LocalDateTime showTime,
            LocalDateTime reserveStartTime,
            LocalDateTime reserveEndTime){

        if(showTime == null){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if(reserveStartTime == null) {
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if(reserveEndTime == null) {
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if( reserveStartTime.isAfter(reserveEndTime) ){
            throw new BusinessException(ErrorCode.RESCHEDULE_ERROR_END_BEFORE_START);
        }
        if( reserveEndTime.isAfter(showTime) ){
            throw new BusinessException(ErrorCode.RESCHEDULE_ERROR_INVALID_SHOWTIME);
        }

        this.showTime          = showTime;
        this.reservation_start = reserveStartTime;
        this.reservation_end   = reserveEndTime;
    }

    /**
     * 명시적으로 method 추가
     * @return
     */
    public boolean isReservable(){
        return this.isReserveAble;
    }

    public void enableReservation(){
        this.isReserveAble = true;
    }
    public void disableReservation(){
        this.isReserveAble = false;
    }

}
