package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
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
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionData map(CreateTransactionDTO transactionDTO) {
        if ( transactionDTO == null ) {
            return null;
        }

        TransactionData.TransactionDataBuilder transactionData = TransactionData.builder();

        transactionData.type( map( transactionDTO.getType() ) );
        transactionData.category( transactionCategoryDTOToTransactionCategory( transactionDTO.getCategory() ) );
        transactionData.otherCategoryDetail( transactionDTO.getOtherCategoryDetail() );
        transactionData.amount( transactionDTO.getAmount() );
        transactionData.description( transactionDTO.getDescription() );

        return transactionData.build();
    }

    @Override
    public TransactionDTO map(Transaction value) {
        if ( value == null ) {
            return null;
        }

        TransactionDTO transactionDTO = new TransactionDTO();

        transactionDTO.setId( map( value.getId() ) );
        transactionDTO.setType( transactionTypeToTransactionTypeDTO( value.getType() ) );
        transactionDTO.setCategory( transactionCategoryToTransactionCategoryDTO( value.getCategory() ) );
        transactionDTO.setOtherCategoryDetail( value.getOtherCategoryDetail() );
        transactionDTO.setAmount( value.getAmount() );
        transactionDTO.setDescription( value.getDescription() );
        transactionDTO.setCreatedAt( value.getCreatedAt() );

        return transactionDTO;
    }

    @Override
    public TransactionPageDTO map(TransactionPage value) {
        if ( value == null ) {
            return null;
        }

        TransactionPageDTO transactionPageDTO = new TransactionPageDTO();

        transactionPageDTO.setItems( transactionListToTransactionDTOList( value.items() ) );
        transactionPageDTO.setPageNumber( value.pageNumber() );
        transactionPageDTO.setPageSize( value.pageSize() );
        transactionPageDTO.setTotalElements( value.totalElements() );
        transactionPageDTO.setTotalPages( value.totalPages() );

        return transactionPageDTO;
    }

    @Override
    public TransactionType map(TransactionTypeDTO type) {
        if ( type == null ) {
            return null;
        }

        TransactionType transactionType;

        switch ( type ) {
            case INCOMING: transactionType = TransactionType.INCOMING;
            break;
            case EXPENSE: transactionType = TransactionType.EXPENSE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + type );
        }

        return transactionType;
    }

    protected TransactionCategory transactionCategoryDTOToTransactionCategory(TransactionCategoryDTO transactionCategoryDTO) {
        if ( transactionCategoryDTO == null ) {
            return null;
        }

        TransactionCategory transactionCategory;

        switch ( transactionCategoryDTO ) {
            case STOCK: transactionCategory = TransactionCategory.STOCK;
            break;
            case TRANSPORT: transactionCategory = TransactionCategory.TRANSPORT;
            break;
            case FOOD: transactionCategory = TransactionCategory.FOOD;
            break;
            case PHONE_CREDIT: transactionCategory = TransactionCategory.PHONE_CREDIT;
            break;
            case RENT: transactionCategory = TransactionCategory.RENT;
            break;
            case EMPLOYEE_SALARY: transactionCategory = TransactionCategory.EMPLOYEE_SALARY;
            break;
            case SALE: transactionCategory = TransactionCategory.SALE;
            break;
            case SERVICE_PROVIDED: transactionCategory = TransactionCategory.SERVICE_PROVIDED;
            break;
            case SALARY: transactionCategory = TransactionCategory.SALARY;
            break;
            case OTHER: transactionCategory = TransactionCategory.OTHER;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + transactionCategoryDTO );
        }

        return transactionCategory;
    }

    protected TransactionTypeDTO transactionTypeToTransactionTypeDTO(TransactionType transactionType) {
        if ( transactionType == null ) {
            return null;
        }

        TransactionTypeDTO transactionTypeDTO;

        switch ( transactionType ) {
            case INCOMING: transactionTypeDTO = TransactionTypeDTO.INCOMING;
            break;
            case EXPENSE: transactionTypeDTO = TransactionTypeDTO.EXPENSE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + transactionType );
        }

        return transactionTypeDTO;
    }

    protected TransactionCategoryDTO transactionCategoryToTransactionCategoryDTO(TransactionCategory transactionCategory) {
        if ( transactionCategory == null ) {
            return null;
        }

        TransactionCategoryDTO transactionCategoryDTO;

        switch ( transactionCategory ) {
            case STOCK: transactionCategoryDTO = TransactionCategoryDTO.STOCK;
            break;
            case TRANSPORT: transactionCategoryDTO = TransactionCategoryDTO.TRANSPORT;
            break;
            case FOOD: transactionCategoryDTO = TransactionCategoryDTO.FOOD;
            break;
            case PHONE_CREDIT: transactionCategoryDTO = TransactionCategoryDTO.PHONE_CREDIT;
            break;
            case RENT: transactionCategoryDTO = TransactionCategoryDTO.RENT;
            break;
            case EMPLOYEE_SALARY: transactionCategoryDTO = TransactionCategoryDTO.EMPLOYEE_SALARY;
            break;
            case SALE: transactionCategoryDTO = TransactionCategoryDTO.SALE;
            break;
            case SERVICE_PROVIDED: transactionCategoryDTO = TransactionCategoryDTO.SERVICE_PROVIDED;
            break;
            case SALARY: transactionCategoryDTO = TransactionCategoryDTO.SALARY;
            break;
            case OTHER: transactionCategoryDTO = TransactionCategoryDTO.OTHER;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + transactionCategory );
        }

        return transactionCategoryDTO;
    }

    protected List<TransactionDTO> transactionListToTransactionDTOList(List<Transaction> list) {
        if ( list == null ) {
            return null;
        }

        List<TransactionDTO> list1 = new ArrayList<TransactionDTO>( list.size() );
        for ( Transaction transaction : list ) {
            list1.add( map( transaction ) );
        }

        return list1;
    }
}
