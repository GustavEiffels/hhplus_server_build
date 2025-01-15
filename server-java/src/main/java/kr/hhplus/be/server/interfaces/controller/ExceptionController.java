package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse handleException(BusinessException e){
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(),e.getErrorStatus());
    }
}
