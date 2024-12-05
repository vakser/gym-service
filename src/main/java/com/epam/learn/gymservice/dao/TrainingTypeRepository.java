package com.epam.learn.gymservice.dao;

import com.epam.learn.gymservice.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer> {
}
