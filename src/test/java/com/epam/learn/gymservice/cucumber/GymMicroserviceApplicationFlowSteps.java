package com.epam.learn.gymservice.cucumber;

import com.epam.learn.gymservice.dao.TraineeRepository;
import com.epam.learn.gymservice.dao.TrainerRepository;
import com.epam.learn.gymservice.dto.*;
import com.epam.learn.gymservice.entity.Trainee;
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
public class GymMicroserviceApplicationFlowSteps {
    private ResponseEntity<ProfileCreatedResponse> trainerProfileCreatedResponse;
    private ResponseEntity<ProfileCreatedResponse> traineeProfileCreatedResponse;
    private ResponseEntity<Void> trainerActivationStatusResponse;
    private ResponseEntity<Void> traineeActivationStatusResponse;
    private ResponseEntity<Void> changeTrainerPasswordResponse;
    private ResponseEntity<Void> changeTraineePasswordResponse;
    private ResponseEntity<GetTrainerProfileResponse> trainerProfileResponse;
    private ResponseEntity<GetTraineeProfileResponse> traineeProfileResponse;
    private JSONObject trainerRegistrationRequestJson;
    private JSONObject traineeRegistrationRequestJson;
    private static String passwordOfRegisteredTrainer;
    private static String passwordOfRegisteredTrainee;
    private static String tokenOfLoggedInTrainer;
    private static String tokenOfLoggedInTrainee;
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TraineeRepository traineeRepository;


    @Given("a user wants to register as a trainer")
    public void aUserWantsToRegisterAsATrainer() {

    }

    @When("a user sends a valid request to register as a trainer")
    public void aUserSendsAValidRequestToBeRegisteredAsATrainer() throws JSONException {
        trainerRegistrationRequestJson = new JSONObject();
        trainerRegistrationRequestJson.put("firstName", "Mike");
        trainerRegistrationRequestJson.put("lastName", "Tyson");
        trainerRegistrationRequestJson.put("specializationId", 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(trainerRegistrationRequestJson.toString(), headers);
        trainerProfileCreatedResponse = restTemplate.postForEntity("http://localhost:8080/api/trainers", request, ProfileCreatedResponse.class);
    }

    @Then("the user is successfully registered as a trainer")
    public void theUserIsSuccessfullyRegisteredAsATrainer() throws JSONException {
        passwordOfRegisteredTrainer = trainerProfileCreatedResponse.getBody().getPassword();
        ProfileCreatedResponse responseDetails = trainerProfileCreatedResponse.getBody();
        Assertions.assertNotNull(responseDetails, "ProfileCreated response should not be null");
        Assertions.assertEquals(HttpStatus.CREATED, trainerProfileCreatedResponse.getStatusCode(),
                "ProfileCreated response status should be 201");
        String usernameThatShouldBeCreated = trainerRegistrationRequestJson.getString("firstName") + "." +
                trainerRegistrationRequestJson.getString("lastName");
        Assertions.assertEquals(usernameThatShouldBeCreated, trainerProfileCreatedResponse.getBody().getUsername(),
                "Response body should contain username Mike.Tyson");
        Assertions.assertFalse(trainerRepository.findByUsername("Mike.Tyson").isEmpty());
    }

    @When("the user sends an invalid request to register as a trainer")
    public void theUserSendsAnInvalidRequestToRegisterAsATrainer() throws JSONException {
        trainerRegistrationRequestJson = new JSONObject();
        trainerRegistrationRequestJson.put("firstName", null);
        trainerRegistrationRequestJson.put("lastName", null);
        trainerRegistrationRequestJson.put("specializationId", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(trainerRegistrationRequestJson.toString(), headers);
        trainerProfileCreatedResponse = restTemplate.postForEntity("http://localhost:8080/api/trainers", request, ProfileCreatedResponse.class);
    }

    @Then("the user is not registered as a trainer and receives response with bad request status")
    public void theUserReceivesResponseWithBadRequestStatus() {
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, trainerProfileCreatedResponse.getStatusCode(),
                "ProfileCreated response status should be 400");
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

    @Then("the message about required authentication is sent to the trainer")
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

    @When("to view profile, trainer sends a request with valid JWT token but other username than own")
    public void toViewProfileTrainerSendsARequestWithValidJWTTokenButOtherUsernameThanOwn() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainer);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        trainerProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainers/Other.Trainer",
                HttpMethod.GET, requestEntity, GetTrainerProfileResponse.class);
    }

    @Then("the response with forbidden status returned to the trainer")
    public void theResponseWithForbiddenStatusReturnedToTheTrainer() {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, trainerProfileResponse.getStatusCode(),
                "Http Status Code 403 Forbidden should have been returned");
    }

    @Given("a user wants to register as a trainee")
    public void aUserWantsToRegisterAsATrainee() {

    }

    @When("a user sends a valid request to register as a trainee")
    public void aUserSendsAValidRequestToRegisterAsATrainee() throws JSONException {
        traineeRegistrationRequestJson = new JSONObject();
        traineeRegistrationRequestJson.put("firstName", "John");
        traineeRegistrationRequestJson.put("lastName", "Jones");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(traineeRegistrationRequestJson.toString(), headers);
        traineeProfileCreatedResponse = restTemplate.postForEntity("http://localhost:8080/api/trainees", request, ProfileCreatedResponse.class);
    }

    @Then("the user is successfully registered as a trainee")
    public void theUserIsSuccessfullyRegisteredAsATrainee() throws JSONException {
        passwordOfRegisteredTrainee = traineeProfileCreatedResponse.getBody().getPassword();
        ProfileCreatedResponse responseDetails = traineeProfileCreatedResponse.getBody();
        Assertions.assertNotNull(responseDetails, "ProfileCreated response should not be null");
        Assertions.assertEquals(HttpStatus.CREATED, traineeProfileCreatedResponse.getStatusCode(),
                "ProfileCreated response status should be 201");
        String usernameThatShouldBeCreated = traineeRegistrationRequestJson.getString("firstName") + "." +
                traineeRegistrationRequestJson.getString("lastName");
        Assertions.assertEquals(usernameThatShouldBeCreated, traineeProfileCreatedResponse.getBody().getUsername(),
                "Response body should contain username John.Jones");
        Assertions.assertFalse(traineeRepository.findByUsername("John.Jones").isEmpty());
    }

    @When("the user sends an invalid request to register as a trainee")
    public void theUserSendsAnInvalidRequestToRegisterAsATrainee() throws JSONException {
        traineeRegistrationRequestJson = new JSONObject();
        traineeRegistrationRequestJson.put("firstName", null);
        traineeRegistrationRequestJson.put("lastName", null);
        traineeRegistrationRequestJson.put("specializationId", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(traineeRegistrationRequestJson.toString(), headers);
        traineeProfileCreatedResponse = restTemplate.postForEntity("http://localhost:8080/api/trainees", request, ProfileCreatedResponse.class);
    }

    @Then("the user is not registered as a trainee and receives response with bad request status")
    public void theUserIsNotRegisteredAsATraineeAndReceivesResponseWithBadRequestStatus() {
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, traineeProfileCreatedResponse.getStatusCode(),
                "ProfileCreated response status should be 400");
    }

    @Given("a trainee named {string} exists in the database")
    public void aTraineeNamedExistsInTheDatabase(String username) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        Assertions.assertTrue(traineeOptional.isPresent(), "Trainee should exist");
    }

    @When("to view profile, trainee {string} sends a request with invalid or without JWT token")
    public void toViewProfileTraineeSendsARequestWithInvalidOrWithoutJWTToken(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        traineeProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainees/" +
                username, HttpMethod.GET, requestEntity, GetTraineeProfileResponse.class);
    }

    @Then("the message about required authentication is sent to the trainee")
    public void theMessageAboutRequiredAuthenticationIsSentToTheTrainee() {
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, traineeProfileResponse.getStatusCode(),
                "Http Status Code 401 Unauthorized should have been returned");
    }

    @And("the trainee {string} is logged in")
    public void theTraineeIsLoggedIn(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("username", username);
        loginCredentials.put("password", passwordOfRegisteredTrainee);
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/api/auth/login", request, AuthResponse.class);
        tokenOfLoggedInTrainee = authResponse.getBody().getToken();
    }

    @When("to view profile, trainee {string} sends a request with valid JWT token")
    public void toViewProfileTraineeSendsARequestWithValidJWTToken(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainee);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        traineeProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainees/" +
                username, HttpMethod.GET, requestEntity, GetTraineeProfileResponse.class);
    }

    @Then("the response with a trainee profile returned to the user")
    public void theResponseWithATraineeProfileReturnedToTheUser() {
        Assertions.assertEquals(HttpStatus.OK, traineeProfileResponse.getStatusCode(),
                "Http Status Code 200 OK should have been returned");
        Assertions.assertEquals("John", traineeProfileResponse.getBody().getFirstName());
        Assertions.assertEquals("Jones", traineeProfileResponse.getBody().getLastName());
    }

    @When("to view profile, trainee sends a request with valid JWT token but other username than own")
    public void toViewProfileTraineeSendsARequestWithValidJWTTokenButOtherUsernameThanOwn() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainee);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        traineeProfileResponse = restTemplate.exchange("http://localhost:8080/api/trainees/Other.Trainee",
                HttpMethod.GET, requestEntity, GetTraineeProfileResponse.class);
    }

    @Then("the response with forbidden status returned to the trainee")
    public void theResponseWithForbiddenStatusReturnedToTheTrainee() {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, traineeProfileResponse.getStatusCode(),
                "Http Status Code 403 Forbidden should have been returned");
    }

    @And("the status of trainer {string} is inactive")
    public void theStatusOfTrainerIsInactive(String username) {
        Trainer trainer = trainerRepository.findByUsername(username).get();
        Assertions.assertFalse(trainer.getUser().getIsActive());
    }

    @When("trainer {string} sends a request with valid JWT token to change the status to active")
    public void trainerSendsARequestWithValidJWTTokenToChangeTheStatusToActive(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainer);
        JSONObject changeActivationStatusRequestBody = new JSONObject();
        changeActivationStatusRequestBody.put("username", username);
        changeActivationStatusRequestBody.put("isActive", true);
        HttpEntity<String> requestEntity = new HttpEntity<>(changeActivationStatusRequestBody.toString(), headers);
        trainerActivationStatusResponse = restTemplate.exchange("http://localhost:8080/api/trainers/" +
                username, HttpMethod.PATCH, requestEntity, Void.class);
    }

    @Then("the status of trainer {string} is changed to active")
    public void theStatusOfTrainerIsChangedToActive(String username) {
        Assertions.assertEquals(HttpStatus.OK, trainerActivationStatusResponse.getStatusCode());
        Trainer trainer = trainerRepository.findByUsername(username).get();
        Assertions.assertTrue(trainer.getUser().getIsActive());
    }

    @And("the status of trainee {string} is inactive")
    public void theStatusOfTraineeIsInactive(String username) {
        Trainee trainee = traineeRepository.findByUsername(username).get();
        Assertions.assertFalse(trainee.getUser().getIsActive());
    }

    @When("trainee {string} sends a request with valid JWT token to change the status to active")
    public void traineeSendsARequestWithValidJWTTokenToChangeTheStatusToActive(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainee);
        JSONObject changeActivationStatusRequestBody = new JSONObject();
        changeActivationStatusRequestBody.put("username", username);
        changeActivationStatusRequestBody.put("isActive", true);
        HttpEntity<String> requestEntity = new HttpEntity<>(changeActivationStatusRequestBody.toString(), headers);
        traineeActivationStatusResponse = restTemplate.exchange("http://localhost:8080/api/trainees/" +
                username, HttpMethod.PATCH, requestEntity, Void.class);
    }

    @Then("the status of trainee {string} is changed to active")
    public void theStatusOfTraineeIsChangedToActive(String username) {
        Assertions.assertEquals(HttpStatus.OK, traineeActivationStatusResponse.getStatusCode());
        Trainee trainee = traineeRepository.findByUsername(username).get();
        Assertions.assertTrue(trainee.getUser().getIsActive());
    }

    @When("trainer {string} sends a request with valid JWT token to change password")
    public void trainerSendsARequestWithValidJWTTokenToChangePassword(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainer);
        JSONObject changePasswordRequestBody = new JSONObject();
        changePasswordRequestBody.put("username", username);
        Trainer trainer = trainerRepository.findByUsername(username).get();
        changePasswordRequestBody.put("oldPassword", trainer.getUser().getPassword());
        changePasswordRequestBody.put("newPassword", "12345");
        HttpEntity<String> requestEntity = new HttpEntity<>(changePasswordRequestBody.toString(), headers);
        changeTrainerPasswordResponse = restTemplate.exchange("http://localhost:8080/api/auth/change-password",
                HttpMethod.PUT, requestEntity, Void.class);
    }

    @Then("the password of trainer is changed")
    public void thePasswordOfTrainerIsChanged() {
        Assertions.assertEquals(HttpStatus.OK, changeTrainerPasswordResponse.getStatusCode());
    }

    @When("trainee {string} sends a request with valid JWT token to change password")
    public void traineeSendsARequestWithValidJWTTokenToChangePassword(String username) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenOfLoggedInTrainee);
        JSONObject changePasswordRequestBody = new JSONObject();
        changePasswordRequestBody.put("username", username);
        Trainee trainee = traineeRepository.findByUsername(username).get();
        changePasswordRequestBody.put("oldPassword", trainee.getUser().getPassword());
        changePasswordRequestBody.put("newPassword", "12345");
        HttpEntity<String> requestEntity = new HttpEntity<>(changePasswordRequestBody.toString(), headers);
        changeTraineePasswordResponse = restTemplate.exchange("http://localhost:8080/api/auth/change-password",
                HttpMethod.PUT, requestEntity, Void.class);
    }

    @Then("the password of trainee is changed")
    public void thePasswordOfTraineeIsChanged() {
        Assertions.assertEquals(HttpStatus.OK, changeTraineePasswordResponse.getStatusCode());
    }
}
