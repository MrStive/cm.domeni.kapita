@e2e @user-event
Feature: User creation from event

  Scenario: Create a user after consuming a creation event
    Given I assume there is no user with the following data in the database
      | id                                   |
      | 234d9cbc-563e-42f1-8de0-31dece250fe8 |
    When An event with following data is received
      | id                                   | event_type   | channel      | name          | firstname | lastname | email                |
      | 234d9cbc-563e-42f1-8de0-31dece250fe8 | USER_CREATED | user-created | john.doe.test | John      | Doe      | john.doe@example.com |
    Then I should see that there is a user with the following data in the database
      | id                                   | name          | firstname | lastname | email                |
      | 234d9cbc-563e-42f1-8de0-31dece250fe8 | john.doe.test | John      | Doe      | john.doe@example.com |
