package kr.hhplus.be.server.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment create(Payment payment){
        return paymentRepository.create(payment);
    }
    public List<Payment> create(List<Payment> paymentList){
        return paymentRepository.create(paymentList);
    }
}
