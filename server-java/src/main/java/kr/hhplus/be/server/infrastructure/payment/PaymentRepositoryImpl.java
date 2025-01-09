package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    @Override
    public Payment create(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public List<Payment> create(List<Payment> payment) {
        return jpaRepository.saveAll(payment);
    }
}
