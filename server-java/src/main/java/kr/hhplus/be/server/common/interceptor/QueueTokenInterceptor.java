package kr.hhplus.be.server.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.interfaces.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
public class QueueTokenInterceptor implements HandlerInterceptor {

    private final QueueTokenFacade queueTokenFacadeImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        QueueTokenFacadeDto.ActiveCheckParam param =
                new QueueTokenFacadeDto.ActiveCheckParam(request.getHeader("Queue_Token"),request.getHeader("UserId"));


        if(!queueTokenFacadeImpl.isValidToken(param).isActive()){
            ApiResponse<String> tokenIsWait = new ApiResponse<>(HttpStatus.FORBIDDEN,"CURRENT WAITING",null);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(tokenIsWait));
            return  false;
        }

        return true;
    }
}
