package kr.hhplus.be.server.interfaces.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ReservationCreateConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    public boolean awaitLatch() throws InterruptedException {
        return latch.await(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "create_reservation",groupId = "DataPlatform")
    public void listen(ConsumerRecord<String,String> record){
        latch.countDown();
        log.info("Received Message  : "+record.value());
    }
}
