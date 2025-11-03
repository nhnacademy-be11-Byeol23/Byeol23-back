package com.nhnacademy.byeol23backend.orderset.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.orderset.order.domain.Order;
import com.nhnacademy.byeol23backend.orderset.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findPaymentByOrder(Order order);

	Optional<Payment> findPaymentByPaymentKey(String paymentKey);
}
