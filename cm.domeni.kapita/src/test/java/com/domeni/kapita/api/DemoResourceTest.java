package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateDemoDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DemoDTO;
import com.domeni.kapita.service.DemoService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class DemoResourceTest {
  @Mock private DemoService demoService;

  @Test
  void fetchAllDemoTest() {
    DemoDTO DemoDTO1 = new DemoDTO().id(UUID.randomUUID());
    DemoDTO DemoDTO2 = new DemoDTO().id(UUID.randomUUID());

    when(demoService.fetchAllDemos()).thenReturn(List.of(DemoDTO1, DemoDTO2));

    // spotless:off
        var demos =
                given()
                        .standaloneSetup(new DemoResource(demoService))
                        .when()
                        .get("/demo")
                        .then()
                        .statusCode(200)
                        .extract().body().jsonPath().getList("$", DemoDTO.class);
        // spotless:on
    assertThat(demos).hasSize(2).containsExactly(DemoDTO1, DemoDTO2);
  }

  @Test
  void createDemoTest() {
    UUID expectedId = UUID.randomUUID();
    DemoDTO demoDTO = new DemoDTO().name("Test Demo");

    when(demoService.createDemo(any(CreateDemoDTO.class))).thenReturn(expectedId);

    // spotless:off
        CreationResponseDTO createdId =
                given()
                        .standaloneSetup(new DemoResource(demoService))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(demoDTO)
                        .when()
                        .post("/demo")
                        .then()
                        .statusCode(201)
                        .extract().body().as(CreationResponseDTO.class);
        // spotless:on
    assertThat(createdId.getNewId()).isEqualTo(expectedId);
  }
}
