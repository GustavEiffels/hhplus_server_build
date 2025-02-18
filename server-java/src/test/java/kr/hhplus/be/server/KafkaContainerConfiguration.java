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

@Configuration
public class KafkaContainerConfiguration {

    private static final KafkaContainer kafka;

    static {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
                .asCompatibleSubstituteFor("apache/kafka"));
        kafka.start();

        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }


    @PostConstruct
    public void createTopic() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafka.getBootstrapServers());
        AdminClient adminClient = AdminClient.create(properties);

        try {
            NewTopic newTopic = new NewTopic("my-first-topic", 1, (short) 1);  // 파티션 1개, Replication Factor 1
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }



    @PreDestroy
    public void preDestroy() {
        if (kafka.isRunning()) {
            kafka.stop();
        }
    }

    @Bean
    public KafkaContainer kafkaContainer() {
        return kafka;
    }
}
