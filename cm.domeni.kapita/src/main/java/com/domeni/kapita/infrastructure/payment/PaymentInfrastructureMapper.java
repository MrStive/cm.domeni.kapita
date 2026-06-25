package com.domeni.kapita.infrastructure.payment;

import cm.domeni.generated.domeni.kapita.payment.dto.InitiatePaymentDTO;
import cm.domeni.generated.domeni.kapita.payment.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.payment.dto.PaymentResponseDTO;
import com.domeni.kapita.domain.payment.PaymentProvider;
import com.domeni.kapita.domain.payment.PaymentRequest;
import com.domeni.kapita.domain.payment.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentInfrastructureMapper {

  @Mapping(target = "externalReference", source = "externalReference")
  @Mapping(target = "purpose", source = "purpose")
  @Mapping(target = "money", expression = "java(toMoneyDto(request.amount(), request.currency()))")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "phoneNumber", source = "phoneNumber")
  @Mapping(target = "provider", source = "provider")
  @Mapping(target = "idempotencyKey", source = "idempotencyKey")
  @Mapping(target = "returnUrl", source = "returnUrl")
  InitiatePaymentDTO toDto(PaymentRequest request);

  @Mapping(target = "paymentId", source = "paymentId")
  @Mapping(target = "externalReference", source = "externalReference")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "paymentUrl", source = "paymentUrl")
  PaymentResponse toDomain(PaymentResponseDTO dto);

  default MoneyDTO toMoneyDto(java.math.BigDecimal amount, String currency) {
    MoneyDTO dto = new MoneyDTO();
    dto.setAmount(amount);
    dto.setCurrency(currency);
    return dto;
  }

  default InitiatePaymentDTO.ProviderEnum map(PaymentProvider provider) {
    return InitiatePaymentDTO.ProviderEnum.fromValue(provider.name());
  }
}
