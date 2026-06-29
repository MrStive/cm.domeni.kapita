package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDurationUnitDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanStatusDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionResponseDTO;
import com.domeni.kapita.domain.payment.PaymentResponse;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T16:55:10+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SubscriptionPlanMapperImpl implements SubscriptionPlanMapper {

    @Override
    public SubscriptionPlanData map(CreateSubscriptionPlanDTO subscriptionPlanDTO) {
        if ( subscriptionPlanDTO == null ) {
            return null;
        }

        SubscriptionPlanData.SubscriptionPlanDataBuilder subscriptionPlanData = SubscriptionPlanData.builder();

        subscriptionPlanData.status( subscriptionPlanStatusDTOToSubscriptionPlanStatus( subscriptionPlanDTO.getStatus() ) );
        subscriptionPlanData.durationValue( subscriptionPlanDTO.getDurationValue() );
        subscriptionPlanData.durationUnit( subscriptionPlanDurationUnitDTOToSubscriptionPlanDurationUnit( subscriptionPlanDTO.getDurationUnit() ) );
        subscriptionPlanData.price( map( subscriptionPlanDTO.getPrice() ) );

        return subscriptionPlanData.build();
    }

    @Override
    public SubscriptionResponseDTO map(PaymentResponse paymentResponse) {
        if ( paymentResponse == null ) {
            return null;
        }

        SubscriptionResponseDTO subscriptionResponseDTO = new SubscriptionResponseDTO();

        if ( paymentResponse.paymentId() != null ) {
            subscriptionResponseDTO.setTransactionId( UUID.fromString( paymentResponse.paymentId() ) );
        }
        subscriptionResponseDTO.setPaymentUrl( paymentResponse.paymentUrl() );

        return subscriptionResponseDTO;
    }

    protected SubscriptionPlanStatus subscriptionPlanStatusDTOToSubscriptionPlanStatus(SubscriptionPlanStatusDTO subscriptionPlanStatusDTO) {
        if ( subscriptionPlanStatusDTO == null ) {
            return null;
        }

        SubscriptionPlanStatus subscriptionPlanStatus;

        switch ( subscriptionPlanStatusDTO ) {
            case ACTIVE: subscriptionPlanStatus = SubscriptionPlanStatus.ACTIVE;
            break;
            case INACTIVE: subscriptionPlanStatus = SubscriptionPlanStatus.INACTIVE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + subscriptionPlanStatusDTO );
        }

        return subscriptionPlanStatus;
    }

    protected SubscriptionPlanDurationUnit subscriptionPlanDurationUnitDTOToSubscriptionPlanDurationUnit(SubscriptionPlanDurationUnitDTO subscriptionPlanDurationUnitDTO) {
        if ( subscriptionPlanDurationUnitDTO == null ) {
            return null;
        }

        SubscriptionPlanDurationUnit subscriptionPlanDurationUnit;

        switch ( subscriptionPlanDurationUnitDTO ) {
            case WEEK: subscriptionPlanDurationUnit = SubscriptionPlanDurationUnit.WEEK;
            break;
            case MONTH: subscriptionPlanDurationUnit = SubscriptionPlanDurationUnit.MONTH;
            break;
            case YEAR: subscriptionPlanDurationUnit = SubscriptionPlanDurationUnit.YEAR;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + subscriptionPlanDurationUnitDTO );
        }

        return subscriptionPlanDurationUnit;
    }
}
