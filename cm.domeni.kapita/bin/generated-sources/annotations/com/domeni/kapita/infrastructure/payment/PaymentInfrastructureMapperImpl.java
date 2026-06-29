package com.domeni.kapita.infrastructure.payment;

import cm.domeni.generated.domeni.kapita.payment.dto.InitiatePaymentDTO;
import cm.domeni.generated.domeni.kapita.payment.dto.PaymentResponseDTO;
import com.domeni.kapita.domain.payment.PaymentRequest;
import com.domeni.kapita.domain.payment.PaymentResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T16:55:10+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PaymentInfrastructureMapperImpl implements PaymentInfrastructureMapper {

    @Override
    public InitiatePaymentDTO toDto(PaymentRequest request) {
        if ( request == null ) {
            return null;
        }

        InitiatePaymentDTO initiatePaymentDTO = new InitiatePaymentDTO();

        initiatePaymentDTO.setExternalReference( request.externalReference() );
        if ( request.purpose() != null ) {
            initiatePaymentDTO.setPurpose( Enum.valueOf( InitiatePaymentDTO.PurposeEnum.class, request.purpose() ) );
        }
        initiatePaymentDTO.setDescription( request.description() );
        initiatePaymentDTO.setPhoneNumber( request.phoneNumber() );
        initiatePaymentDTO.setProvider( map( request.provider() ) );
        initiatePaymentDTO.setIdempotencyKey( request.idempotencyKey() );
        initiatePaymentDTO.setReturnUrl( request.returnUrl() );

        initiatePaymentDTO.setMoney( toMoneyDto(request.amount(), request.currency()) );

        return initiatePaymentDTO;
    }

    @Override
    public PaymentResponse toDomain(PaymentResponseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PaymentResponse.PaymentResponseBuilder paymentResponse = PaymentResponse.builder();

        if ( dto.getPaymentId() != null ) {
            paymentResponse.paymentId( dto.getPaymentId().toString() );
        }
        paymentResponse.externalReference( dto.getExternalReference() );
        paymentResponse.status( dto.getStatus() );
        paymentResponse.paymentUrl( dto.getPaymentUrl() );

        return paymentResponse.build();
    }
}
