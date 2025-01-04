package kr.hhplus.be.server.persentation.controller.concert;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public interface ConcertApiDto {

    @Getter
    @Builder
    class AvailableDateReq{
        private Long userId;
        private Long tokenId;
        private Long concertId;
    }

    @Getter
    @Builder
    class AvailableDateRes{
        private String message;
        private int cnt;
        private ConcertInfoDto concert_info;
        private List<ScheduleInfo> schedule_list;

        @Getter
        @Builder
        static class ConcertInfoDto{
            private String name;
            private String performer;
        }

        @Getter
        @Builder
        static class ScheduleInfo{
            private int schedule_id;
            private String date;
            private String reservation_start;
            private String reservation_end;
            private int leftTicket;
        }
    }



    @Getter
    @Builder
    class LeftSeatRes {

        private String message;
        private int cnt;
        private ScheduleInfo schedule_info;
        private List<SeatInfo> seat_list;

        @Getter
        @Builder
        public static class ScheduleInfo {
            private String date;
            private String reservation_start;
            private String reservation_end;
        }

        @Getter
        @Builder
        public static class SeatInfo {
            private String seat_num;
            private int seat_id;
        }
    }
    @Getter
    @Builder
    class LeftSeatReq{
        private Long userId;
        private Long tokenId;
        private Long scheduleId;
    }
}
