package kr.hhplus.be.server.common.exceptions;


public class BusinessException extends RuntimeException{
    private final ErrorCode code;

    public BusinessException(ErrorCode status, String message){
        super(status.getMessage()+" : "+message);
        this.code = status;
    }

    public BusinessException(ErrorCode status){
        super(status.getMessage());
        this.code = status;
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
