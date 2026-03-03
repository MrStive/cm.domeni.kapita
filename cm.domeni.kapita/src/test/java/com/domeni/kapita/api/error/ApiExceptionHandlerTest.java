package com.domeni.kapita.api.error;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.domeni.kapita.api.DemoResource;
import com.domeni.kapita.domain.exception.DemoNotFoundException;
import com.domeni.kapita.domain.exception.InvalidDemoPayloadException;
import com.domeni.kapita.service.DemoService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DemoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
@TestPropertySource(
    properties = {
      "spring.cloud.config.enabled=false",
      "spring.cloud.config.import-check.enabled=false",
      "spring.cloud.bus.enabled=false",
      "spring.cloud.stream.enabled=false",
      "kapita.security.jwt.public-key-location=classpath:security/jwt-public.pem",
      "kapita.security.jwt.issuer=http://auth-service.local",
      "kapita.security.jwt.audience=kapita-api",
    })
class ApiExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DemoService demoService;

  @Test
  void fetchDemoByIdWhenDemoDoesNotExistShouldReturnNotFoundPayloadTest() throws Exception {
    UUID unknownDemoId = UUID.randomUUID();
    when(demoService.getByDemoId(unknownDemoId)).thenThrow(new DemoNotFoundException());

    mockMvc
        .perform(get("/demo/{demoId}", unknownDemoId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("KAPITA-404-001"))
        .andExpect(jsonPath("$.message").value("demo not found"))
        .andExpect(jsonPath("$.path").value("/demo/" + unknownDemoId))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void createDemoWhenPayloadIsInvalidShouldReturnValidationPayloadTest() throws Exception {
    mockMvc
        .perform(post("/demo").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/demo"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(demoService);
  }

  @Test
  void createDemoWhenServiceRejectsPayloadShouldReturnDomainPayloadTest() throws Exception {
    when(demoService.createDemo(any()))
        .thenThrow(new InvalidDemoPayloadException("create demo payload is invalid"));

    mockMvc
        .perform(post("/demo").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-002"))
        .andExpect(jsonPath("$.message").value("create demo payload is invalid"))
        .andExpect(jsonPath("$.path").value("/demo"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
