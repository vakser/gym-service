package com.epam.learn.gymservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainerProfileResponse {
    private String firstName;
    private String lastName;
    private Integer specializationId;
    private Boolean isActive;
    private List<TraineeListResponse> traineesList;
}
