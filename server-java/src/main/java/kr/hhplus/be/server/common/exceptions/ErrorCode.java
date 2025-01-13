package kr.hhplus.be.server.common.exceptions;

public enum ErrorCode {
    // COMMON
    REQUIRE_FIELD_MISSING(" [ REQUIRE FIELD MISSING ]","필수 필드가 누락 되었습니다."),

    // USER
    NOT_FOUND_USER("[ NOT FOUND USER ]", "사용자를 찾을 수 없습니다."),
    INSUFFICIENT_BALANCE("[ INSUFFICIENT BALANCE ]", "잔액이 부족합니다. POINT 범위 : 0 ~ 100,000,000 point"),
    MAXIMUM_POINT_EXCEEDED("[ MAXIMUM POINT EXCEEDED ]", "포인트 보유량 한도를 초과하였습니다. POINT 범위 : 0 ~ 100,000,000 point"),




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
