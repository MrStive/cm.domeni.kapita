package com.domeni.kapita.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.domeni.kapita.e2e.context.FetchFlowContext;
import com.domeni.kapita.e2e.support.JwtTokenFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;

public class DemoFetchSteps {

  @LocalServerPort private int localServerPort;

  @Autowired private JdbcClient jdbcClient;

  @Autowired private JwtTokenFactory jwtTokenFactory;

  @Autowired private FetchFlowContext fetchFlowContext;

  @Given("Assume that I am connected with a token containing read scopes")
  public void assumeThatIAmConnectedWithATokenContainingReadScopes(DataTable dataTable) {
    List<String> scopes =
        dataTable.asMaps(String.class, String.class).stream()
            .map(row -> row.get("scope"))
            .filter(scope -> scope != null && !scope.isBlank())
            .toList();

    assertThat(scopes).as("At least one read scope must be provided").isNotEmpty();

    fetchFlowContext.setBearerToken(jwtTokenFactory.createToken(scopes.toArray(new String[0])));

    assertThat(fetchFlowContext.getBearerToken())
        .as("A bearer token must be available before any operation")
        .isNotBlank();
  }

  @Given("Assume that following Liquibase demos are present in database")
  public void assumeThatFollowingLiquibaseDemosArePresentInDatabase(DataTable dataTable) {
    List<Map<String, String>> expectedRows =
        normalizeRows(dataTable.asMaps(String.class, String.class));

    List<Map<String, String>> actualRows =
        normalizeRows(
            jdbcClient
                .sql(
                    "select c_id as id, c_name as name from t_demo where c_name like 'seed-demo-%'"
                        + " and c_deleted = false")
                .query()
                .listOfRows()
                .stream()
                .map(
                    row ->
                        Map.of(
                            "id", String.valueOf(row.get("id")),
                            "name", String.valueOf(row.get("name"))))
                .toList());

    assertThat(actualRows)
        .as("Seeded rows from Liquibase must exactly match the DataTable")
        .containsExactlyElementsOf(expectedRows);

    fetchFlowContext.setSeededDemoId(UUID.fromString(expectedRows.getFirst().get("id")));
    fetchFlowContext.setSeededDemoName(expectedRows.getFirst().get("name"));
  }

  @When("I call fetch all demos API")
  public void iCallFetchAllDemosApi() {
    assertThat(fetchFlowContext.getBearerToken())
        .as("A bearer token must be available before any operation")
        .isNotBlank();

    fetchFlowContext.setLastResponse(
        given()
            .port(localServerPort)
            .auth()
            .oauth2(fetchFlowContext.getBearerToken())
            .accept(ContentType.JSON)
            .when()
            .get("/demo"));
  }

  @When("I call fetch demo by id API for {string}")
  public void iCallFetchDemoByIdApiFor(String demoId) {
    assertThat(fetchFlowContext.getBearerToken())
        .as("A bearer token must be available before any operation")
        .isNotBlank();

    fetchFlowContext.setLastResponse(
        given()
            .port(localServerPort)
            .auth()
            .oauth2(fetchFlowContext.getBearerToken())
            .accept(ContentType.JSON)
            .when()
            .get("/demo/{demoId}", UUID.fromString(demoId)));
  }

  @Then("the fetch response status should be {int}")
  public void theFetchResponseStatusShouldBe(int expectedStatus) {
    assertThat(fetchFlowContext.getLastResponse()).isNotNull();
    assertThat(fetchFlowContext.getLastResponse().statusCode()).isEqualTo(expectedStatus);
  }

  @Then("I should see following demos in fetch all response")
  public void iShouldSeeFollowingDemosInFetchAllResponse(DataTable dataTable) {
    List<Map<String, String>> expectedRows =
        normalizeRows(dataTable.asMaps(String.class, String.class));
    Set<String> expectedIds =
        expectedRows.stream().map(row -> row.get("id")).collect(Collectors.toSet());

    List<Map<String, String>> actualRows =
        normalizeRows(
            fetchFlowContext.getLastResponse().jsonPath().getList("$", Map.class).stream()
                .map(
                    row ->
                        Map.of(
                            "id", String.valueOf(row.get("id")),
                            "name", String.valueOf(row.get("name"))))
                .filter(row -> expectedIds.contains(row.get("id")))
                .toList());

    assertThat(actualRows).containsExactlyElementsOf(expectedRows);
  }

  @Then("I should not see demo id {string} in fetch all response")
  public void iShouldNotSeeDemoIdInFetchAllResponse(String hiddenDemoId) {
    List<String> responseIds =
        fetchFlowContext.getLastResponse().jsonPath().getList("id", String.class);

    assertThat(responseIds).doesNotContain(hiddenDemoId);
  }

  @Then("I should see following demo in fetch by id response")
  public void iShouldSeeFollowingDemoInFetchByIdResponse(DataTable dataTable) {
    Map<String, String> expectedRow = dataTable.asMaps(String.class, String.class).getFirst();

    assertThat(fetchFlowContext.getLastResponse().jsonPath().getString("id"))
        .isEqualTo(expectedRow.get("id"));
    assertThat(fetchFlowContext.getLastResponse().jsonPath().getString("name"))
        .isEqualTo(expectedRow.get("name"));
  }

  private List<Map<String, String>> normalizeRows(List<Map<String, String>> rows) {
    return rows.stream()
        .map(row -> Map.of("id", row.get("id"), "name", row.get("name")))
        .sorted(Comparator.comparing(row -> row.get("id")))
        .toList();
  }
}
