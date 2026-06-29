package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import com.domeni.kapita.domain.user.UserCreationData;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T16:55:10+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserEventMapperImpl implements UserEventMapper {

    @Override
    public UserCreationData map(UserCreatedEventDTO source) {
        if ( source == null ) {
            return null;
        }

        UserCreationData.UserCreationDataBuilder userCreationData = UserCreationData.builder();

        userCreationData.id( source.getId() );
        userCreationData.name( source.getUsername() );
        userCreationData.firstname( source.getFirstname() );
        userCreationData.lastname( source.getLastname() );
        userCreationData.email( map( source.getEmail() ) );

        return userCreationData.build();
    }
}
