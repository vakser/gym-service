Feature: Add training and send workload message

  Scenario: Add a new training for a trainer
    Given a trainer named "John.Doe" and a trainee named "Jane.Smith" exist in the database
    And a training request for trainer "John.Doe" with trainee "Jane.Smith"
    When the training is added
    Then the training should be saved in the main test database
    And a workload message should be sent to the queue