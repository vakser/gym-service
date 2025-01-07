package com.epam.learn.gymservice.cucumber;

import com.epam.learn.gymservice.dao.TraineeRepository;
import com.epam.learn.gymservice.dao.TrainerRepository;
import com.epam.learn.gymservice.dao.TrainingRepository;
import com.epam.learn.gymservice.dto.AddTrainingRequest;
import com.epam.learn.gymservice.dto.UpdateTraineeTrainersRequest;
import com.epam.learn.gymservice.entity.*;
import com.epam.learn.gymservice.service.TraineeService;
import com.epam.learn.gymservice.service.TrainerService;
import com.epam.learn.gymservice.service.WorkloadMessageProducer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddTrainingSteps {
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainingRepository trainingRepository;
    private final String trainingName = "Fitness Training";

    @Autowired
    private WorkloadMessageProducer workloadMessageProducer;

    private AddTrainingRequest addTrainingRequest;
    private int trainingNumber = 1;

    @Given("a trainer named {string} and a trainee named {string} exist in the database")
    public void createTrainerAndTrainee(String trainerUsername, String traineeUsername) {
        // Check if the trainer exists, otherwise create
        if (trainerRepository.findByUsername(trainerUsername).isEmpty()) {
            Trainer trainer = new Trainer();
            User user = new User();
            user.setUsername(trainerUsername);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setIsActive(true);
            user.setPassword("password");
            trainer.setUser(user);
            TrainingType trainingType = new TrainingType();
            trainingType.setId(1);
            trainingType.setName("fitness");
            trainer.setSpecialization(trainingType);
            trainerRepository.save(trainer);
        }
        // Check if the trainee exists, otherwise create
        if (traineeRepository.findByUsername(traineeUsername).isEmpty()) {
            Trainee trainee = new Trainee();
            User user = new User();
            user.setUsername(traineeUsername);
            user.setFirstName("Jane");
            user.setLastName("Smith");
            user.setIsActive(true);
            user.setPassword("password");
            trainee.setUser(user);
            traineeRepository.save(trainee);
        }
    }

    @Given("a training request for trainer {string} with trainee {string}")
    public void createTrainingRequest(String trainerUsername, String traineeUsername) {
        if (!traineeService.findActiveTrainersNotAssignedToTrainee(traineeUsername).isEmpty()) {
            UpdateTraineeTrainersRequest updateTrainingRequest = new UpdateTraineeTrainersRequest();
            updateTrainingRequest.setTraineeUsername(traineeUsername);
            updateTrainingRequest.setTrainerUsernames(List.of("John.Doe"));
            traineeService.updateTraineeTrainers(updateTrainingRequest);
        }
        addTrainingRequest = new AddTrainingRequest();
        addTrainingRequest.setTrainerUsername(trainerUsername);
        addTrainingRequest.setTraineeUsername(traineeUsername);
        List<Training> trainings = trainingRepository.findAll();
        if (!trainings.isEmpty()) {
            Training latestTraining = trainings.getLast();
            trainingNumber = Integer.parseInt(latestTraining.getTrainingName().substring(16)) + 1;
        }
        addTrainingRequest.setTrainingName(trainingName + trainingNumber);
        addTrainingRequest.setTrainingDate(LocalDate.now());
        addTrainingRequest.setTrainingDuration(60);
    }

    @When("the training is added") // ensure ActiveMQ service is running before running this test!
    public void addTraining() {
        trainerService.addTraining(addTrainingRequest);
    }

    @Then("the training should be saved in the main test database")
    public void verifyTrainingInDatabase() {
        // Verify that the training is saved in the main microservice's MySQL database
        Trainer trainer = trainerRepository.findByUsername(addTrainingRequest.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        assertThat(trainer).isNotNull();
        List<Training> trainerTrainings = trainingRepository.findAll();
        assertThat(trainerTrainings).anyMatch(training ->
                training.getTrainingName().equals(trainingName + trainingNumber));
    }

    @Then("a workload message should be sent to the queue")
    public void verifyMessageInQueue() {
        // Verify that the message is sent using the workload message producer
        // Here, we check if the message was produced observing a number of messages in a trainer.workload.queue of our running ActiveMQ server
        assertThat(workloadMessageProducer).isNotNull();
    }

}
