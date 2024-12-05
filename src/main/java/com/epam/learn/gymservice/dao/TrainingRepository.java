package com.epam.learn.gymservice.dao;

import com.epam.learn.gymservice.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingRepository extends JpaRepository<Training, Integer>, JpaSpecificationExecutor<Training> {

}
