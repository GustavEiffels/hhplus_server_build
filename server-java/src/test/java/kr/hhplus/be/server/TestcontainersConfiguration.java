package kr.hhplus.be.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@Configuration
public class TestcontainersConfiguration {

	private static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
				.withDatabaseName("hhplus")
				.withUsername("test")
				.withPassword("test")
				.withExposedPorts(3306);
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
	}

	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		hikariConfig.setUsername(MYSQL_CONTAINER.getUsername());
		hikariConfig.setPassword(MYSQL_CONTAINER.getPassword());
		hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

		// HikariCP 성능 최적화 옵션
		hikariConfig.setMaximumPoolSize(150); // 최대 커넥션 개수
		hikariConfig.setMinimumIdle(50); // 최소 유휴 커넥션 개수
		hikariConfig.setIdleTimeout(10000); // 유휴 커넥션 최대 유지 시간 (ms)	\
		hikariConfig.setConnectionTimeout(10000); // 커넥션 획득 타임아웃 (ms)
		hikariConfig.setValidationTimeout(10000); // 검증 타임아웃 (ms)
		hikariConfig.setLeakDetectionThreshold(10000); // 커넥션 누수 감지 시간 (ms)

		return new HikariDataSource(hikariConfig);
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}
}