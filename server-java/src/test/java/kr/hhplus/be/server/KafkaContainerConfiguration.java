package kr.hhplus.be.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.util.Collections;
import java.util.Properties;

//@Configuration
public class KafkaContainerConfiguration {

    // KafkaContainer 객체들 선언 (각각의 Kafka 브로커를 다루기 위해 3개 필요)
    private static final KafkaContainer kafkaCluster1;
    private static final KafkaContainer kafkaCluster2;
    private static final KafkaContainer kafkaCluster3;

    static {
        // 각 Kafka 클러스터(브로커) 초기화
        kafkaCluster1 = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaCluster2 = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaCluster3 = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));


        // Kafka 클러스터들을 시작
        kafkaCluster1.start();
        kafkaCluster2.start();
        kafkaCluster3.start();

        // Kafka 브로커들에 대한 bootstrap server 설정
        System.setProperty("spring.kafka.bootstrap-servers",
                kafkaCluster1.getBootstrapServers() + "," + kafkaCluster2.getBootstrapServers() + "," + kafkaCluster3.getBootstrapServers());
    }

    @PostConstruct
    public void createTopic() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", System.getProperty("spring.kafka.bootstrap-servers"));
        AdminClient adminClient = AdminClient.create(properties);

        try {
            // 새로운 Kafka 토픽 생성
            NewTopic newTopic = new NewTopic("my-first-topic", 3, (short) 1);  // 3개의 파티션, Replication Factor 3
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    @PreDestroy
    public void preDestroy() {
        // Kafka 클러스터 중지
        if (kafkaCluster1.isRunning()) {
            kafkaCluster1.stop();
        }
        if (kafkaCluster2.isRunning()) {
            kafkaCluster2.stop();
        }
        if (kafkaCluster3.isRunning()) {
            kafkaCluster3.stop();
        }
    }

    @Bean
    public KafkaContainer kafkaCluster1() {
        return kafkaCluster1;
    }

    @Bean
    public KafkaContainer kafkaCluster2() {
        return kafkaCluster2;
    }

    @Bean
    public KafkaContainer kafkaCluster3() {
        return kafkaCluster3;
    }
}
