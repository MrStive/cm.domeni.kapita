package com.domeni.kapita.infrastructure.payment;

import cm.domeni.generated.domeni.kapita.payment.api.PaymentApi;
import cm.domeni.generated.domeni.kapita.payment.dto.PaymentResponseDTO;
import com.domeni.kapita.domain.payment.PaymentPort;
import com.domeni.kapita.domain.payment.PaymentRequest;
import com.domeni.kapita.domain.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentPort {
  private final PaymentApi paymentApi;
  private final PaymentInfrastructureMapper mapper;

  @Override
  public PaymentResponse initiatePayment(PaymentRequest request) {
    ResponseEntity<PaymentResponseDTO> response = paymentApi.initiatePayment(mapper.toDto(request));
    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new RuntimeException("Failed to initiate payment");
    }
    return mapper.toDomain(response.getBody());
  }
}
