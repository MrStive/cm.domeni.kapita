package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateDemoDTO;
import cm.domeni.generated.domeni.kapita.dto.DemoDTO;
import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoData;
import com.domeni.kapita.domain.demo.DemoId;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T16:55:11+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DemoMapperImpl implements DemoMapper {

    @Override
    public DemoData map(CreateDemoDTO demoDTO) {
        if ( demoDTO == null ) {
            return null;
        }

        DemoData.DemoDataBuilder demoData = DemoData.builder();

        demoData.name( demoDTO.getName() );

        return demoData.build();
    }

    @Override
    public DemoDTO map(Demo demo) {
        if ( demo == null ) {
            return null;
        }

        DemoDTO demoDTO = new DemoDTO();

        String value = demoIdValue( demo );
        if ( value != null ) {
            demoDTO.setId( UUID.fromString( value ) );
        }
        demoDTO.setName( map( demo.getName() ) );

        return demoDTO;
    }

    private String demoIdValue(Demo demo) {
        DemoId id = demo.getId();
        if ( id == null ) {
            return null;
        }
        return id.getValue();
    }
}
