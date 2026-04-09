package com.domeni.kapita.service.mapper;

import cm.lao.generated.domeni.kapita.event.dto.EmailAddressDTO;
import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import com.domeni.kapita.domain.user.UserCreationData;
import java.util.Optional;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserEventMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "username")
  @Mapping(target = "firstname", source = "firstname")
  @Mapping(target = "lastname", source = "lastname")
  @Mapping(target = "email", source = "email")
  UserCreationData map(UserCreatedEventDTO source);

  default String map(EmailAddressDTO emailAddressDTO) {
    return Optional.ofNullable(emailAddressDTO).map(EmailAddressDTO::getEmail).orElse(null);
  }
}
