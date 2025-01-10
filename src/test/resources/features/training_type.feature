Feature: Training Type Service

  Scenario: Fetching all training types
    Given the following training types exist:
      | id  | name         |
      | 1   | Cardio       |
      | 2   | Strength     |
    When a user fetches all training types
    Then the following training types should be returned:
      | id  | name         |
      | 1   | Cardio       |
      | 2   | Strength     |
