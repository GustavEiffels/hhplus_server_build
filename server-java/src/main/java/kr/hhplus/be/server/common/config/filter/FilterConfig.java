package kr.hhplus.be.server.common.config.filter;

import kr.hhplus.be.server.common.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter(){
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LogFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return  registrationBean;
    }
}
