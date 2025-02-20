package kr.hhplus.be.server.interfaces.consumer;


import kr.hhplus.be.server.application.data_platform.DataPlatformFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationCreateConsumer {

    private final DataPlatformFacade dataPlatformFacade;

    private CountDownLatch latch = new CountDownLatch(1);

    public boolean awaitLatch() throws InterruptedException {
        return latch.await(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "create_reservation",groupId = "DataPlatform")
    public void listenCreateReservation(ConsumerRecord<String,String> record){
        latch.countDown();
        log.info("Received Message - create_reservation  : {}",record.value());
        dataPlatformFacade.getEventFromReservationCreate(record.key(),record.value());
    }
}
