package kr.hhplus.be.server.common.exceptions;


import lombok.Getter;

public class BusinessException extends RuntimeException{
    private final ErrorCode code;
    @Getter
    private final String errorMessage;

    public BusinessException(ErrorCode status, String message){
        super(status.getMessage()+" : "+message);
        this.errorMessage = status.getMessage()+" : "+message;
        this.code = status;
    }

    public BusinessException(ErrorCode status){
        super(status.getMessage());
        this.code = status;
        this.errorMessage = status.getMessage();
    }

    public ErrorCode getErrorStatus(){
        return code;
    }

    public String getErrorCodeString(){
        return code.getStatus();
    }

    @Override
    public String getMessage() {
        return code.getMessage();
    }
}
