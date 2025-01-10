package com.epam.learn.gymservice.cucumber;

import com.epam.learn.gymservice.dao.TrainingTypeRepository;
import com.epam.learn.gymservice.entity.TrainingType;
import com.epam.learn.gymservice.service.TrainingTypeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

@CucumberContextConfiguration
@SpringBootTest
public class TrainingTypeSteps {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @MockBean
    private TrainingTypeRepository trainingTypeRepository;

    private List<TrainingType> fetchedTrainingTypes;

    @Given("the following training types exist:")
    public void the_following_training_types_exist(List<TrainingType> trainingTypes) {
        Mockito.when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);
    }

    @When("a user fetches all training types")
    public void a_fetches_all_training_types() {
        fetchedTrainingTypes = trainingTypeService.getAllTrainingTypes();
    }

    @Then("the following training types should be returned:")
    public void the_following_training_types_should_be_returned(List<TrainingType> expectedTrainingTypes) {
        checkTrainingTypesInList(fetchedTrainingTypes, expectedTrainingTypes);
    }

    private void checkTrainingTypesInList(List<TrainingType> actualList, List<TrainingType> expectedTypes) {
        for (TrainingType expected : expectedTypes) {
            boolean exists = actualList.stream()
                    .anyMatch(trainingType -> trainingType.getId().equals(expected.getId()) && trainingType.getName().equals(expected.getName()));
            assertTrue(exists, "Expected TrainingType with id " + expected.getId() + " and name " + expected.getName() + " was not found.");
        }
    }
}
