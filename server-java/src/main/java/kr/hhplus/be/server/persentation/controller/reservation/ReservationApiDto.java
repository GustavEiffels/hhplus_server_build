package kr.hhplus.be.server.persentation.controller.reservation;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public interface ReservationApiDto {

    @Getter
    @Builder
    class PurchaseReq{
        private Long userId;
        private Long tokenId;
        private List<Long> reservation_list;
    }

    @Getter
    @Builder
    class PurchaseRes {
        private String message;
        private int quantity;
        private List<ReservationInfo> reservation_list;

        @Getter
        @Builder
        public static class ReservationInfo {
            private int reservation_id;
            private int seat_num;
            private String concert_name;
            private String concert_performer;
            private String concert_time;
            private int price;
        }
    }


    @Getter
    @Builder
    class ReserveDtoReq{
        private int userId;
        private int tokenId;
        private int seatId;
    }
    @Getter
    @Builder
    class ReserveDtoRes{
        private String message;
        private ReservedInfo reserved_info;


        @Getter
        @Builder
        static class ReservedInfo{
            private int reservation_id;
            private int seat_number;
            private String status;
            private int price;
            private String concert_name;
            private String concert_performer;
            private String expiredAt;
        }
    }
}
