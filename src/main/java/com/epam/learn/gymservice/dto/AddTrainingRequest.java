package com.epam.learn.gymservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTrainingRequest {
    @NotBlank(message = "Trainee username is mandatory")
    private String traineeUsername;
    @NotBlank(message = "Trainer username is mandatory")
    private String trainerUsername;
    @NotBlank(message = "Training name is mandatory")
    private String trainingName;
    @NotNull(message = "Training date is mandatory")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration is mandatory")
    private Integer trainingDuration;
}
