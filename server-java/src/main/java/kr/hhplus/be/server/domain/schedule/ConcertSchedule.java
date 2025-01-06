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
    @Min(0)
    @Max(50)
    private int leftTicket = 50;  // 최대 50 개

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
            throw new BusinessException(ErrorCode.Entity,"[콘서트] 정보는 필수적으로 입력 되어야합니다.");
        }
        reschedule(showTime,reserveStartTime,reserveEndTime);
        this.concert = concert;
    }

    public void reschedule(
            LocalDateTime showTime,
            LocalDateTime reserveStartTime,
            LocalDateTime reserveEndTime){

        if(showTime == null){
            throw new BusinessException(ErrorCode.Entity,"[공연 시작] 시간은 필수 값 입니다.");
        }
        if(reserveStartTime == null) {
            throw new BusinessException(ErrorCode.Entity,"[예약 시작] 시간은 필수 값 입니다.");
        }
        if(reserveEndTime == null) {
            throw new BusinessException(ErrorCode.Entity,"[예약 종료] 시간은 필수 값 입니다.");
        }
        if( reserveStartTime.isAfter(reserveEndTime) ){
            throw new BusinessException(ErrorCode.Entity,"[예약 종료] 시간은 [예약 시작] 시간보다 늦어야 합니다.");
        }
        if( reserveEndTime.isAfter(showTime) ){
            throw new BusinessException(ErrorCode.Entity,"[공연] 시간은 [예약 종료] 시간보다 늦어야 합니다.");
        }

        this.showTime          = showTime;
        this.reservation_start = reserveStartTime;
        this.reservation_end   = reserveEndTime;
    }

    public void increaseTicketCnt(){
        if(this.leftTicket==50){
            throw new BusinessException(ErrorCode.Entity,"더 이상 티켓 수를 늘릴 수 없습니다.");
        }
        this.leftTicket++;
    }
    public void decreaseTicketCnt(){
        if(this.leftTicket==0){
            throw new BusinessException(ErrorCode.Entity,"남은 티켓 수량이 [0] 개 입니다.");
        }
        this.leftTicket--;
    }

}
