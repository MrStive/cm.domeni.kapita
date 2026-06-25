package com.domeni.kapita.domain.payment;

public interface PaymentPort {
  PaymentResponse initiatePayment(PaymentRequest request);
}
