package kr.hhplus.be.server.common.exceptions;

public enum ErrorCode {
    // COMMON
    REQUIRE_FIELD_MISSING(" [ REQUIRE FIELD MISSING ]","필수 필드가 누락 되었습니다."),

    // USER
    NOT_FOUND_USER("[ NOT FOUND USER ]", "사용자를 찾을 수 없습니다."),
    INSUFFICIENT_BALANCE("[ INSUFFICIENT BALANCE ]", "잔액이 부족합니다. POINT 범위 : 0 ~ 100,000,000 point"),
    MAXIMUM_POINT_EXCEEDED("[ MAXIMUM POINT EXCEEDED ]", "포인트 보유량 한도를 초과하였습니다. POINT 범위 : 0 ~ 100,000,000 point"),


    // CONCERT
    NOT_FOUND_CONCERT("[ NOT FOUND CONCERT ]", "공연을 찾을 수 없습니다."),
    EXPIRE_QUEUE_TOKEN("[ EXPIRE QUEUE TOKEN ]","대기열이 만료된 토큰 입니다."),
    NOT_MATCHED_WITH_USER("[ NOT MATCHED WITH USER ]","사용자가 대기열 토큰과 매칭되지 않습니다."),


    // CONCERT SCHEDULE
    NOT_FOUND_CONCERT_SCHEDULE("[ NOT FOUND CONCERT SCHEDULE ]", "공연 일정을 찾을 수 없습니다."),
    RESCHEDULE_ERROR_END_BEFORE_START("[ RESCHEDULE ERROR END BEFORE START ]","[예약 종료] 시간은 [예약 시작] 시간보다 늦어야 합니다."),
    RESCHEDULE_ERROR_INVALID_SHOWTIME("[ RESCHEDULE ERROR INVALID SHOWTIME ]","[공연] 시간은 [예약 종료] 시간보다 늦어야 합니다."),

    RESERVATION_END("[ RESERVATION END ]","해당 예약은 마감되었습니다."),
    NOT_RESERVABLE_TIME("[ NOT RESERVABLE TIME ]","예약 가능한 시간이 아닙니다."),


    // TOKEN
    NOT_FOUND_QUEUE_TOKEN("[ NOT FOUND QUEUE TOKEN ]", "대기열 토큰을 찾을 수 없습니다."),

    // PAYMENT
    NOT_VALID_PAYMENT_AMOUNT("[ NOT VALID PAYMENT AMOUNT ]","결제 금액은 0 보다 커야합니다."),

    // RESERVATION
    NOT_FOUND_RESERVATION("[ NOT_FOUND_RESERVATION ]","예약을 찾을 수 없습니다."),

    EXPIRE_RESERVATION("[ EXPIRE_RESERVATION ]","만료된 예약 입니다."),
    NOT_RESERVATION_OWNER("[ NOT RESERVATION OWNER ]","예약한 사용자가 일치하지 않습니다."),







    Entity("DOMAIN ENTITY Error","[ 테이블 정책에 어긋나는 값이 감지 되었습니다. ]"),
    Repository("REPOSITORY Error","[ 데이터를 찾는 과정에서 예외 상황이 감지 되었습니다. ]"),
    INVALID_INPUT("INVALID INPUT Error", "[ 유효하지 않은 입력 값이 감지되었습니다. ]"),
    SERVICE("SERVICE Error", "[서비스 결과]");




    private final String status;

    private String message;

    ErrorCode(String status, String message){
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", status, message);
    }

}
