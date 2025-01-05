package kr.hhplus.be.server.common.exceptions;

public enum ErrorCode {
    Entity("Data Setting Error","[ 테이블 정책에 어긋나는 값이 감지 되었습니다. ]");


    private final String status;

    private String message;

    ErrorCode(String status, String message){
        this.status = status;
        this.message = message;
    }

    public String getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }


}
