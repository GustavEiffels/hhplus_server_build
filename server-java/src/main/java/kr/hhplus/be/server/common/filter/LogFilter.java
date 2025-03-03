package kr.hhplus.be.server.common.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class LogFilter extends OncePerRequestFilter {
    private static final ThreadLocal<String> uuidHolder = new ThreadLocal<>();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uuid    = "REQUEST-"+UUID.randomUUID();

        CachingRequestWrapper requestWrapper = new CachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();
        try {
            logRequest(requestWrapper,uuid);
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logResponse(responseWrapper,uuid, startTime);
            responseWrapper.copyBodyToResponse();
            uuidHolder.remove();
        }
    }

    private void logRequest(CachingRequestWrapper request, String uuid) throws IOException {
        String body = getBody(request.getInputStream());
        log.info("UUID - [{}] | RequestBody : {}",uuid,body);
    }

    private void logResponse(ContentCachingResponseWrapper response, String uuid, long startTime) throws IOException {
        String body = getBody(response.getContentInputStream());
        log.info("UUID - [{}] | ResponseBody : {} | processed in {} ms",uuid,body,(System.currentTimeMillis() - startTime));
    }

    public String getBody(InputStream is) throws IOException {
        byte[] content = StreamUtils.copyToByteArray(is);
        if (content.length == 0) {
            return null;
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    public static String getCurrentUUID() {
        return uuidHolder.get();
    }
}
