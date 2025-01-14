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
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "schedule_id",
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ConcertSchedule concertSchedule;


    @NotNull
    private SeatStatus status = SeatStatus.RESERVABLE;


    @Builder // 기본 생성
    public Seat(int seatNo, Long price, ConcertSchedule concertSchedule){
        if( seatNo < 1 || seatNo > 50){
            throw new BusinessException(ErrorCode.INVALID_SEAT_NUMBER);
        }
        if( price < 10_000L || price > 1_000_000L ){
            throw new BusinessException(ErrorCode.INVALID_SEAT_PRICE);
        }
        if( concertSchedule == null ){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }

        this.seatNo = seatNo;
        this.price  = price;
        this.concertSchedule = concertSchedule;
    }

    public void reserve(){
        this.status = SeatStatus.RESERVED;
    }

    public void reservable(){
        this.status = SeatStatus.RESERVABLE;
    }





}
