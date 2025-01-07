package kr.hhplus.be.server.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.persentation.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
public class QueueTokenInterceptor implements HandlerInterceptor {

    private final QueueTokenFacade queueTokenFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Long queueTokenId = Long.parseLong(request.getHeader("Queue_Token"));
        Long userId       = Long.parseLong(request.getHeader("UserId"));

        if(queueTokenFacade.isValidToken(queueTokenId,userId)){
            ApiResponse<String> tokenIsWait = new ApiResponse<>(HttpStatus.FORBIDDEN,"현재 대기열 대기 중 입니다.",null);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(tokenIsWait));
            return  false;
        }

        return true;
    }
}
