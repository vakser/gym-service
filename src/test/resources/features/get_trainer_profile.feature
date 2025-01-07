Feature: Get profile data for a trainer requires JWT token

  Scenario: TrainerRegistration
    Given a user wants to register as a trainer
    When a user sends a valid request to register
    Then the user is successfully registered as a trainer

  Scenario: Getting profile data for a trainer from DB not authenticated when a valid JWT token not provided
    Given a trainer named "Mike.Tyson" exists in the database
    When to view profile, trainer "Mike.Tyson" sends a request with invalid or without JWT token
    Then the message about required authentication is sent to the user

  Scenario: Getting profile data for a trainer from DB successful when the trainer is logged in
    Given a trainer named "Mike.Tyson" exists in the database
    And the trainer "Mike.Tyson" is logged in
    When to view profile, trainer "Mike.Tyson" sends a request with valid JWT token
    Then the response with a trainer profile returned to the user