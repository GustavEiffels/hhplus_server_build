package kr.hhplus.be.server.common.exceptions;

public enum ErrorCode {
    Entity("DOMAIN ENTITY Error","[ 테이블 정책에 어긋나는 값이 감지 되었습니다. ]"),
    Repository("REPOSITORY Error","[ 데이터를 찾는 과정에서 예외 상황이 감지 되었습니다. ]"),
    INVALID_INPUT("INVALID INPUT Error", "[ 유효하지 않은 입력 값이 감지되었습니다. ]"),
    SERVICE("SERVICE Error", "[서비스 결과]"),

    // USER
    NotFoundUser("[ NOT FOUND USER ]","사용자를 찾을 수 없습니다.");


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
