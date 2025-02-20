package kr.hhplus.be.server.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutBoxService {
    private final OutBoxRepository outBoxRepository;

    // CREATE
    public OutBox create(OutBox outBox){
        return outBoxRepository.create(outBox);
    }


}
