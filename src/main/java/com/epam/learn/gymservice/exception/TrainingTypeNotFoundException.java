package com.epam.learn.gymservice.exception;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String message) {
        super(message);
    }
}
