package kr.hhplus.be.server.infrastructure;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.data = data;
        this.code = status.value();
        this.message = message;
        this.status = status;
    }

    public static <T> ApiResponse<T> of(HttpStatus status,String message,T data) {
        return new ApiResponse<>(status,message,data);
    }

    public static <T> ApiResponse<T> of(HttpStatus status,T data) {
        return of(status,status.name(),data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK,"OK", data);
    }
}
