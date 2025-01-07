package com.epam.learn.gymservice.cucumber;

import com.epam.learn.gymservice.dao.TrainerRepository;
import com.epam.learn.gymservice.dto.*;
import com.epam.learn.gymservice.entity.Trainer;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetTrainerProfileByRegisteredTrainerSteps {
    private ResponseEntity<ProfileCreatedResponse> profileCreatedResponse;
    private ResponseEntity<GetTrainerProfileResponse> trainerProfileResponse;
    private JSONObject trainerRegistrationRequestJson;
    private static String passwordOfRegisteredTrainer;
    private static String tokenOfLoggedInTrainer;
    TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private TrainerRepository trainerRepository;


    @Given("a user wants to register as a trainer")
    public void aUserWantsToRegisterAsATrainer() {

    }

    @When("a user sends a valid request to register")
    public void aUserSendsAValidRequestToBeRegisteredAsATrainer() throws JSONException {
        trainerRegistrationRequestJson = new JSONObject();
        trainerRegistrationRequestJson.put("firstName", "Mike");
        trainerRegistrationRequestJson.put("lastName", "Tyson");
        trainerRegistrationRequestJson.put("specializationId", 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(trainerRegistrationRequestJson.toString(), headers);
        profileCreatedResponse = restTemplate.postForEntity("http://localhost:8080/api/trainers", request, ProfileCreatedResponse.class);
    }

    @Then("the user is successfully registered as a trainer")
    public void theUserIsSuccessfullyRegisteredAsATrainer() throws JSONException {
        passwordOfRegisteredTrainer = profileCreatedResponse.getBody().getPassword();
        ProfileCreatedResponse responseDetails = profileCreatedResponse.getBody();
        Assertions.assertNotNull(responseDetails, "ProfileCreated response should not be null");
        Assertions.assertEquals(HttpStatus.CREATED, profileCreatedResponse.getStatusCode(),
                "ProfileCreated response status should be 201");
        String usernameThatShouldBeCreated = trainerRegistrationRequestJson.getString("firstName") + "." +
                trainerRegistrationRequestJson.getString("lastName");
        Assertions.assertEquals(usernameThatShouldBeCreated, profileCreatedResponse.getBody().getUsername(),
                "Response body should contain username Mike.Tyson");
        Assertions.assertFalse(trainerRepository.findByUsername("Mike.Tyson").isEmpty());
    }

    @Given("a trainer named {string} exists in the database")
    public void aTrainerNamedExistsInTheDatabase(String username) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        Assertions.assertTrue(trainerOptional.isPresent(), "Trainer should exist");
    }

    @When("to view profile, trainer {string} sends a request with invalid or without JWT token")
    public void toViewProfileTrainerSendsARequestWithInvalidOrWithoutJWTToken(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        trainerProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainers/" +
                username, HttpMethod.GET, requestEntity, GetTrainerProfileResponse.class);
    }

    @Then("the message about required authentication is sent to the user")
    public void theMessageAboutRequiredAuthenticationIsSentToTheUser() {
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, trainerProfileResponse.getStatusCode(),
                "Http Status Code 401 Unauthorized should have been returned");
    }

    @And("the trainer {string} is logged in")
    public void theTrainerIsLoggedIn(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("username", username);
        loginCredentials.put("password", passwordOfRegisteredTrainer);
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/api/auth/login", request, AuthResponse.class);
        tokenOfLoggedInTrainer = authResponse.getBody().getToken();
    }

    @When("to view profile, trainer {string} sends a request with valid JWT token")
    public void toViewProfileTrainerSendsARequestWithValidJWTToken(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainer);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        trainerProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainers/" +
                username, HttpMethod.GET, requestEntity, GetTrainerProfileResponse.class);
    }

    @Then("the response with a trainer profile returned to the user")
    public void theResponseWithATrainerProfileReturnedToTheUser() {
        Assertions.assertEquals(HttpStatus.OK, trainerProfileResponse.getStatusCode(),
                "Http Status Code 200 OK should have been returned");
        Assertions.assertEquals("Mike", trainerProfileResponse.getBody().getFirstName());
        Assertions.assertEquals("Tyson", trainerProfileResponse.getBody().getLastName());
    }
}
