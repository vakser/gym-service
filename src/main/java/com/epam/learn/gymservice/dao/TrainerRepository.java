package com.epam.learn.gymservice.dao;

import com.epam.learn.gymservice.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    @Query("SELECT t FROM Trainer t WHERE t.user.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);

}
