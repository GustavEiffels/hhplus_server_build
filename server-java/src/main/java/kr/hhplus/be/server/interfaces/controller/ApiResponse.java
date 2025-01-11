package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    @Schema(description = "상태 코드",example = "200")
    private int code;

    @Schema(description = "Http 상태",example = "OK")
    private HttpStatus status;

    @Schema(description = "결과에 대한 메세지",example = "Success")
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
