package com.epam.learn.gymservice.cucumber;

import com.epam.learn.gymservice.entity.TrainingType;
import io.cucumber.java.DataTableType;

import java.util.Map;


public class TrainingTypeParameterType {
    @DataTableType
    public TrainingType trainingTypeEntry(Map<String, String> entry) {
        return new TrainingType(
                Integer.parseInt(entry.get("id")),
                entry.get("name")
        );
    }
}
