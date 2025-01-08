package kr.hhplus.be.server.domain.seat;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.domain.schedule.ConcertSchedule;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seat_id")
    private Long id;

    @NotNull
    private int seatNo;

    @NotNull
    private int price;

    @ManyToOne
    @JoinColumn(
            name = "schedule_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ConcertSchedule concertSchedule;


// update often
    private LocalDateTime expiredAt;

    @NotNull
    private SeatStatus status = SeatStatus.RESERVABLE;


    @Builder // 기본 생성
    public Seat(int seatNo, int price, ConcertSchedule concertSchedule){
        if( seatNo < 1 || seatNo > 50){
            throw new BusinessException(ErrorCode.Entity,"[좌석 번호] 는 1 ~ 50 사이의 자연수여야 합니다.");
        }
        if( price < 10_000 || price > 100_000 ){
            throw new BusinessException(ErrorCode.Entity,"[금액] 은 10,000 원에서 100,000 사이로 책정 되어야 합니다.");
        }
        if( concertSchedule == null ){
            throw new BusinessException(ErrorCode.Entity,"[콘서트 스케줄] 정보는 필수로 할당이 되어야 합니다.");
        }

        this.seatNo = seatNo;
        this.price  = price;
        this.concertSchedule = concertSchedule;
    }

    public void updateStatus(SeatStatus status){
        if( status.equals(SeatStatus.OCCUPIED) ){
            this.expiredAt = LocalDateTime.now().plusMinutes(5);
        }
        else{
            this.expiredAt = null;
        }
        this.status = status;
    }





}
