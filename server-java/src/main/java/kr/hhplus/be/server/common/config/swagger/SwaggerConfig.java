package kr.hhplus.be.server.common.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "콘서트 예약 서비스",
                description = "항해 플러스에서 지정한 콘서트 예약 서비스의 OPEN API 페이지 입니다.",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return null;
    }
}
