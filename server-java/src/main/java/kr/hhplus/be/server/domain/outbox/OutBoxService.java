package kr.hhplus.be.server.domain.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutBoxService {
    private final OutBoxRepository outBoxRepository;

    public void create(OutBox outBox){
        outBoxRepository.create(outBox);
    }

    public void create(List<OutBox> outBox){
        outBoxRepository.create(outBox);
    }
}
