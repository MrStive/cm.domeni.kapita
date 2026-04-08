package com.domeni.kapita.domain.user;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserCreationData(
    UUID id, String name, String firstname, String lastname, String email) {}
