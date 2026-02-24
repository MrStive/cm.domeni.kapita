@e2e @fetch
Feature: Demo fetch API

  Scenario: Fetch seeded demos with a valid JWT token
    Given Assume that I am connected with a token containing read scopes
      | scope         |
      | demo:read:all |
      | demo:read     |
    And Assume that following Liquibase demos are present in database
      | id                                   | name        |
      | 11111111-1111-1111-1111-111111111111 | seed-demo-1 |
      | 22222222-2222-2222-2222-222222222222 | seed-demo-2 |
    When I call fetch all demos API
    Then the fetch response status should be 200
    And I should see following demos in fetch all response
      | id                                   | name        |
      | 11111111-1111-1111-1111-111111111111 | seed-demo-1 |
      | 22222222-2222-2222-2222-222222222222 | seed-demo-2 |
    And I should not see demo id "33333333-3333-3333-3333-333333333333" in fetch all response
    When I call fetch demo by id API for "11111111-1111-1111-1111-111111111111"
    Then the fetch response status should be 200
    And I should see following demo in fetch by id response
      | id                                   | name        |
      | 11111111-1111-1111-1111-111111111111 | seed-demo-1 |
