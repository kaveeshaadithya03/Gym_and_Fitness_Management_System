package com.example.gym.service;

import com.example.gym.model.TrainerProfile;
import com.example.gym.model.User;
import com.example.gym.repository.TrainerProfileRepository;
import com.example.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerProfileService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final UserRepository userRepository;

    public TrainerProfile createTrainerProfile(Long userId, String specialization, String certifications, Integer experience, Double hourlyRate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TrainerProfile profile = TrainerProfile.builder()
                .user(user)
                .specialization(specialization)
                .bio(certifications) // Use bio field for certifications
                .hourlyRate(hourlyRate)
                .build();

        return trainerProfileRepository.save(profile);
    }

    public TrainerProfile updateTrainerProfile(Long trainerId, String specialization, String certifications, Integer experience, Double hourlyRate) {
        TrainerProfile profile = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        if (specialization != null) profile.setSpecialization(specialization);
        if (certifications != null) profile.setBio(certifications); // Use bio field for certifications
        if (hourlyRate != null) profile.setHourlyRate(hourlyRate);

        return trainerProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public TrainerProfile getTrainerProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return trainerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found for user"));
    }
}