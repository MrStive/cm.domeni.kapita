@e2e @create
Feature: Demo creation API

  Scenario: Create a demo with a valid JWT token
    Given Assume that I am connected with a token containing create scopes
      | scope       |
      | demo:create |
    When I call create demo API with payload
      | name            |
      | demo-e2e-create |
    Then the create response status should be 201
    And a created demo id is returned
    And I should see the created demo in database
      | name            |
      | demo-e2e-create |
