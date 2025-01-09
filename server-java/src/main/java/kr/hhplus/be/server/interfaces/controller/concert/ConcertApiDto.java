package kr.hhplus.be.server.interfaces.controller.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


public interface ConcertApiDto {

    @Getter
    @Builder
    class FindScheduleRequest{
        @Schema(description = "콘서트 id",example = "1")
        private Long concertId;
    }

    @Getter
    @Builder
    class FindScheduleResponse{
        @Schema(description = "가능한 날짜 수",example = "2")
        private int cnt;

        @Schema(description = "콘서트 정보")
        private ConcertInfoDto concert_info;

        @Schema(description = "예약 가능한 콘서트 스케줄 리스트",example = "[\n" +
                "      {\n" +
                "        \"schedule_id\": 12,\n" +
                "        \"date\": \"2025-03-01T12:00:00.000Z\",\n" +
                "        \"reservation_start\": \"2025-02-01T12:00:00.000Z\",\n" +
                "        \"reservation_end\": \"2025-02-21T12:00:00.000Z \",\n" +
                "        \"leftTicket\": 50\n" +
                "      },\n" +
                "      {\n" +
                "        \"schedule_id\": 21,\n" +
                "        \"date\": \"2025-03-11T12:00:00.000Z\",\n" +
                "        \"reservation_start\": \"2025-02-11T12:00:00.000Z\",\n" +
                "        \"reservation_end\": \"2025-02-25T12:00:00.000Z \",\n" +
                "        \"leftTicket\": 30\n" +
                "      }\n" +
                "    ]\n")
        private List<ScheduleInfo> schedule_list;



        @Getter
        @Builder
        static class ConcertInfoDto{
            @Schema(description = "콘서트 이름",example = "오징어 먹물쇼!")
            private String name;
            @Schema(description = "콘서트 정보",example = "오징이")
            private String performer;
        }

        @Getter
        @Builder
        static class ScheduleInfo{
            @Schema(description = "콘서트 스케줄 id")
            private int schedule_id;

            @Schema(description = "콘서트 날짜")
            private LocalDateTime date;

            @Schema(description = "에약 시작 시간")
            private LocalDateTime reservation_start;

            @Schema(description = "에약 종료 시간")
            private LocalDateTime reservation_end;

            @Schema(description = "남은 티켓 수")
            private int leftTicket;
        }
    }



    @Getter
    @Builder
    class LeftSeatRes {

        @Schema(description = "남은 티켓 수",example = "2")
        private int cnt;

        @Schema(description = "콘서트 날짜",example = "2025-03-01T12:00:00.000Z")
        private LocalDateTime date;
        @Schema(description = "콘서트 예약 시작 날짜",example = "2025-02-11T12:00:00.000Z")
        private LocalDateTime reservation_start;
        @Schema(description = "콘서트 예약 종료 날짜",example = "2025-02-21T12:00:00.000Z")
        private LocalDateTime reservation_end;

        @Schema(description = "예약 가능한 좌석 리스트", example = "[\n" +
                "      {\n" +
                "        \"seat_num\": \"32\",\n" +
                "        \"seat_id\": 132\n" +
                "      },\n" +
                "      {\n" +
                "        \"seat_num\": \"25\",\n" +
                "        \"seat_id\": 33\n" +
                "      }\n" +
                "    ]")
        private List<SeatInfo> seat_list;



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
        @Schema(description = "사용자 id",example = "3")
        private Long userId;

        @Schema(description = "대기열 토큰 id",example = "2")
        private Long tokenId;

        @Schema(description = "예약 가능한 콘서트 스케줄 날짜",example = "2025-03-01T12:00:00.000Z")
        private LocalDateTime date;
    }
}
