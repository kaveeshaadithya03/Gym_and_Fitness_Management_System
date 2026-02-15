package com.example.gym.repository;

import com.example.gym.model.TrainerProfile;
import com.example.gym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    Optional<TrainerProfile> findByUser(User user);

    // These two were missing → added now
    List<TrainerProfile> findBySpecializationContainingIgnoreCase(String specialization);

    List<TrainerProfile> findByLocationContainingIgnoreCase(String location);
}