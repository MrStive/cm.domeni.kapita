package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class DebtResourceTest {
  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private DebtService debtService;

  @Test
  void createDebtShouldReturnCreatedDebtIdTest() {
    UUID debtId = UUID.randomUUID();
    UUID currentUserId = UUID.randomUUID();
    CreateDebtDTO input =
        new CreateDebtDTO()
            .type(DebtTypeDTO.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000.00")))
            .dueDate(LocalDate.of(2026, 4, 11));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(debtService.createDebt(any(CreateDebtDTO.class), any(UserId.class))).thenReturn(debtId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(input)
                .when()
                        .post("/debt")
                .then()
                        .statusCode(201)
                        .extract().body().as(CreationResponseDTO.class);
        // spotless:on
    assertThat(response.getNewId()).isEqualTo(debtId);
  }
}
