package kr.hhplus.be.server.domain.payment;

import java.util.List;

public interface PaymentRepository {
    Payment create(Payment payment);
    List<Payment> create(List<Payment> payment);
}
