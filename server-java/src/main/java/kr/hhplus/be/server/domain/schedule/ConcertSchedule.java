package kr.hhplus.be.server.domain.schedule;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Name;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.domain.concert.Concert;
import lombok.AccessLevel;
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
    private int           leftTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id",nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Concert concert;


}
