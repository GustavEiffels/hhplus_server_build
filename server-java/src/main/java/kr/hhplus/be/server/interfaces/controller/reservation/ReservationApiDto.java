package kr.hhplus.be.server.interfaces.controller.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public interface ReservationApiDto {


    @Getter
    @Builder
    class ReserveDtoReq{
        @Schema(description = "사용자 id",example = "12")
        private int userId;

        @Schema(description = "대기열 토큰 id",example = "20")
        private int tokenId;

        @Schema(description = "좌석 id",example = "20")
        private int seatId;

        @Schema(description = "예약한 공연 시간",example = "2025-03-01T12:00:00.780Z")
        private LocalDateTime showTime;
    }
    @Getter
    @Builder
    class ReserveDtoRes{
        @Schema(description = "예약 id",example = "20")
        private int reservation_id;

        @Schema(description = "예약 좌석 번호",example = "31")
        private int seat_number;

        @Schema(description = "예약 상태",example = "Reserved")
        private ReservationStatus status;

        @Schema(description = "예약한 좌석의 가격",example = "100000")
        private int price;

        private String concert_name;

        @Schema(description = "콘서트 이름",example = "오징어의 먹물쇼!")
        private String concert_performer;

        @Schema(description = "만료 시간",example = "2025-03-01T12:00:00.000Z")
        private LocalDateTime expiredAt;
    }
}
