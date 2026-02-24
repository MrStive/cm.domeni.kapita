package com.domeni.kapita.service.mapper;

import cm.lao.generated.domeni.kapita.dto.CreateDemoDTO;
import cm.lao.generated.domeni.kapita.dto.DemoDTO;
import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoData;
import com.domeni.kapita.domain.demo.DemoName;
import java.util.Optional;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DemoMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "name", source = "name")
  DemoData map(CreateDemoDTO demoDTO);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "name")
  DemoDTO map(Demo demo);

  default String map(DemoName value) {
    return Optional.ofNullable(value).map(DemoName::getValue).orElse(null);
  }
}
