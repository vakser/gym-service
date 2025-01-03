package com.epam.learn.gymservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private Integer specializationId;
    @NotNull(message = "Status of activation is mandatory")
    private Boolean isActive;
}
