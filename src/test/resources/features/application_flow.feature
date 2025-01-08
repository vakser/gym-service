Feature: Gym microservice application flow

  Scenario: TrainerRegistration
    Given a user wants to register as a trainer
    When a user sends a valid request to register as a trainer
    Then the user is successfully registered as a trainer

  Scenario: Attempt to register as a trainer with invalid request
    Given a user wants to register as a trainer
    When the user sends an invalid request to register as a trainer
    Then the user is not registered as a trainer and receives response with bad request status

  Scenario: Getting profile data for a trainer from DB not authenticated when a valid JWT token not provided
    Given a trainer named "Mike.Tyson" exists in the database
    When to view profile, trainer "Mike.Tyson" sends a request with invalid or without JWT token
    Then the message about required authentication is sent to the trainer

  Scenario: Getting profile data for a trainer from DB successful when the trainer is logged in
    Given a trainer named "Mike.Tyson" exists in the database
    And the trainer "Mike.Tyson" is logged in
    When to view profile, trainer "Mike.Tyson" sends a request with valid JWT token
    Then the response with a trainer profile returned to the user

  Scenario: Getting profile data for a trainer from DB forbidden when the trainer is logged in but is trying to get data for other trainer
    Given a trainer named "Mike.Tyson" exists in the database
    And the trainer "Mike.Tyson" is logged in
    When to view profile, trainer sends a request with valid JWT token but other username than own
    Then the response with forbidden status returned to the trainer

  Scenario: TraineeRegistration
    Given a user wants to register as a trainee
    When a user sends a valid request to register as a trainee
    Then the user is successfully registered as a trainee

  Scenario: Attempt to register as a trainee with invalid request
    Given a user wants to register as a trainee
    When the user sends an invalid request to register as a trainee
    Then the user is not registered as a trainee and receives response with bad request status

  Scenario: Getting profile data for a trainee from DB not authenticated when a valid JWT token not provided
    Given a trainee named "John.Jones" exists in the database
    When to view profile, trainee "John.Jones" sends a request with invalid or without JWT token
    Then the message about required authentication is sent to the trainee

  Scenario: Getting profile data for a trainee from DB successful when the trainee is logged in
    Given a trainee named "John.Jones" exists in the database
    And the trainee "John.Jones" is logged in
    When to view profile, trainee "John.Jones" sends a request with valid JWT token
    Then the response with a trainee profile returned to the user

  Scenario: Getting profile data for a trainee from DB forbidden when the trainee is logged in but is trying to get data for other trainee
    Given a trainee named "John.Jones" exists in the database
    And the trainee "John.Jones" is logged in
    When to view profile, trainee sends a request with valid JWT token but other username than own
    Then the response with forbidden status returned to the trainee

  Scenario: Changing activation status of a trainer
    Given a trainer named "Mike.Tyson" exists in the database
    And the trainer "Mike.Tyson" is logged in
    And the status of trainer "Mike.Tyson" is inactive
    When trainer "Mike.Tyson" sends a request with valid JWT token to change the status to active
    Then the status of trainer "Mike.Tyson" is changed to active

  Scenario: Changing activation status of a trainee
    Given a trainee named "John.Jones" exists in the database
    And the trainee "John.Jones" is logged in
    And the status of trainee "John.Jones" is inactive
    When trainee "John.Jones" sends a request with valid JWT token to change the status to active
    Then the status of trainee "John.Jones" is changed to active