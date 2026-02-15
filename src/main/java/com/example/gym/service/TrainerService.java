package com.example.gym.service;

import com.example.gym.model.TrainerProfile;
import com.example.gym.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerProfileRepository trainerProfileRepository;

    public List<TrainerProfile> searchBySpecialization(String specialization) {
        return trainerProfileRepository.findBySpecializationContainingIgnoreCase(specialization);
    }

    public List<TrainerProfile> searchByLocation(String location) {
        return trainerProfileRepository.findByLocationContainingIgnoreCase(location);
    }

    public TrainerProfile getTrainer(Long id) {
        return trainerProfileRepository.findById(id).orElse(null);
    }

    public List<TrainerProfile> findAll() {
        return trainerProfileRepository.findAll();
    }
}