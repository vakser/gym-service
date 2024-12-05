package com.epam.learn.gymservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TraineeListResponse {
    private String username;
    private String firstName;
    private String lastName;
}
