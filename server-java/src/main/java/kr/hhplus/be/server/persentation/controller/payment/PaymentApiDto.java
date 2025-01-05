package kr.hhplus.be.server.persentation.controller.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public interface PaymentApiDto {

    @Getter
    @Builder
    class PurchaseReq{
        @Schema(description = "사용자 아이디",example = "1")
        private Long userId;

        @Schema(description = "토큰 아이디",example = "12")
        private Long tokenId;

        @Schema(description = "예약 id 리스트",example = "[1,21]")
        private List<Long> reservation_list;
    }

    @Getter
    @Builder
    class PurchaseRes {
        @Schema(description = "결제에 대한 메세지",example = "결제 성공")
        private String message;

        @Schema(description = "결제한 예약 수량",example = "2")
        private int quantity;

        @Schema(description = "결제한 예약 내역",example = "[" +
                "      {\n" +
                "        \"reservation_id\": 1,\n" +
                "        \"seat_num\": 2,\n" +
                "        \"concert_name\": \"서커스  쇼!\",\n" +
                "        \"concert_performer\": \"김연습\",\n" +
                "        \"concert_time\": \"2025-03-01T12:00:00\",\n" +
                "        \"price\": 100000\n" +
                "      }," +
                "      {\n" +
                "        \"reservation_id\": 21,\n" +
                "        \"seat_num\": 31,\n" +
                "        \"concert_name\": \"오징어 먹물쇼!\",\n" +
                "        \"concert_performer\": \"오징이\",\n" +
                "        \"concert_time\": \"2025-03-01T16:00:00\",\n" +
                "        \"price\": 10000\n" +
                "      }" +
                "]")
        private List<PaymentApiDto.PurchaseRes.ReservationInfo> reservation_list;

        @Getter
        @Builder
        public static class ReservationInfo {

            @Schema(description = "결제한 예약 번호")
            private int reservation_id;
            @Schema(description = "좌석 번호")
            private int seat_num;
            @Schema(description = "콘서트 이름")
            private String concert_name;
            @Schema(description = "콘서트 공연자 이름")
            private String concert_performer;
            @Schema(description = "콘서트 시간")
            private String concert_time;
            @Schema(description = "가격")
            private int price;
        }
    }
}
