package com.domeni.kapita.e2e.context;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ScenarioScope
public class FetchFlowContext {
  private String bearerToken;
  private Response lastResponse;
  private UUID seededDemoId;
  private String seededDemoName;
}
