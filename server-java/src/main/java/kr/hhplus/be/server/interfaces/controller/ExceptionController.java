package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.filter.LogFilter;
import kr.hhplus.be.server.interfaces.scheduler.SchedulerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse handleException(BusinessException e){
        String uuid = (SchedulerContext.getUuid() != null) ? SchedulerContext.getUuid() : LogFilter.getCurrentUUID();
        log.info("UUID - [{}] | ERROR STATUS = {} , ERROR MESSAGE = {}",uuid, e.getErrorStatus(),e.getErrorMessage());
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(),e.getErrorStatus());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e){
        String uuid = (SchedulerContext.getUuid() != null) ? SchedulerContext.getUuid() : LogFilter.getCurrentUUID();
        HttpStatus status;
        if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        }else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.info("UUID - [{}] | ERROR STATUS = {} , ERROR MESSAGE = {}",uuid, status,e.getLocalizedMessage());
        return ApiResponse.of(status, e.getMessage());
    }
}
