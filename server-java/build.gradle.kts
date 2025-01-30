plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {

	// redisson
	implementation("org.redisson:redisson:3.22.0")


	// querydsl : 2025-01-06
	implementation ("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor ("com.querydsl:querydsl-apt:${dependencyManagement.importedProperties["querydsl.version"]}:jakarta")

	annotationProcessor ("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor ("jakarta.persistence:jakarta.persistence-api")

	// swagger : 2025-01-05
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

	// validation-api : 2025-01-04
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")


    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.24") // Lombok 의존성 추가
	annotationProcessor("org.projectlombok:lombok:1.18.24") // Lombok 애노테이션 프로세서 추가


	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")


}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}
