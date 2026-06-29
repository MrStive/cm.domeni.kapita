package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtStatusDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.debt.DebtType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T16:55:11+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DebtMapperImpl implements DebtMapper {

    @Override
    public DebtData map(CreateDebtDTO debtDTO) {
        if ( debtDTO == null ) {
            return null;
        }

        DebtData.DebtDataBuilder debtData = DebtData.builder();

        debtData.type( map( debtDTO.getType() ) );
        debtData.counterpartyName( debtDTO.getCounterpartyName() );
        debtData.amount( map( debtDTO.getAmount() ) );
        debtData.dueDate( debtDTO.getDueDate() );

        return debtData.build();
    }

    @Override
    public DebtDTO map(Debt value) {
        if ( value == null ) {
            return null;
        }

        DebtDTO debtDTO = new DebtDTO();

        debtDTO.setId( map( value.getId() ) );
        debtDTO.setType( debtTypeToDebtTypeDTO( value.getType() ) );
        debtDTO.setCounterpartyName( value.getCounterpartyName() );
        debtDTO.setAmount( map( value.getAmount() ) );
        debtDTO.setDueDate( value.getDueDate() );
        debtDTO.setStatus( debtStatusToDebtStatusDTO( value.getStatus() ) );
        debtDTO.setCreatedAt( value.getCreatedAt() );

        return debtDTO;
    }

    @Override
    public DebtPageDTO map(DebtPage value) {
        if ( value == null ) {
            return null;
        }

        DebtPageDTO debtPageDTO = new DebtPageDTO();

        debtPageDTO.setItems( debtListToDebtDTOList( value.items() ) );
        debtPageDTO.setPageNumber( value.pageNumber() );
        debtPageDTO.setPageSize( value.pageSize() );
        debtPageDTO.setTotalElements( value.totalElements() );
        debtPageDTO.setTotalPages( value.totalPages() );

        return debtPageDTO;
    }

    @Override
    public DebtType map(DebtTypeDTO type) {
        if ( type == null ) {
            return null;
        }

        DebtType debtType;

        switch ( type ) {
            case PAYABLE: debtType = DebtType.PAYABLE;
            break;
            case RECEIVABLE: debtType = DebtType.RECEIVABLE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + type );
        }

        return debtType;
    }

    protected DebtTypeDTO debtTypeToDebtTypeDTO(DebtType debtType) {
        if ( debtType == null ) {
            return null;
        }

        DebtTypeDTO debtTypeDTO;

        switch ( debtType ) {
            case RECEIVABLE: debtTypeDTO = DebtTypeDTO.RECEIVABLE;
            break;
            case PAYABLE: debtTypeDTO = DebtTypeDTO.PAYABLE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + debtType );
        }

        return debtTypeDTO;
    }

    protected DebtStatusDTO debtStatusToDebtStatusDTO(DebtStatus debtStatus) {
        if ( debtStatus == null ) {
            return null;
        }

        DebtStatusDTO debtStatusDTO;

        switch ( debtStatus ) {
            case UNPAID: debtStatusDTO = DebtStatusDTO.UNPAID;
            break;
            case PAID: debtStatusDTO = DebtStatusDTO.PAID;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + debtStatus );
        }

        return debtStatusDTO;
    }

    protected List<DebtDTO> debtListToDebtDTOList(List<Debt> list) {
        if ( list == null ) {
            return null;
        }

        List<DebtDTO> list1 = new ArrayList<DebtDTO>( list.size() );
        for ( Debt debt : list ) {
            list1.add( map( debt ) );
        }

        return list1;
    }
}
