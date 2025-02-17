package kr.hhplus.be.server.interfaces.controller.reservation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaPlatformConsumer {

    @Getter
    private String receivedMessage = null;
    private CountDownLatch latch = new CountDownLatch(1);


    public boolean awaitLatch() throws InterruptedException {
        return latch.await(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "my-first-topic",groupId = "my-consumer-group")
    public void listen(ConsumerRecord<String,String> record){
        log.info("Received Message  : "+record.value());
        this.receivedMessage = record.value();
    }
}
