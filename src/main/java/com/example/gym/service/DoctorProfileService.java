package com.example.gym.service;

import com.example.gym.model.DoctorProfile;
import com.example.gym.model.User;
import com.example.gym.repository.DoctorProfileRepository;
import com.example.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;

    public DoctorProfile createDoctorProfile(Long userId, String specialization, String licenseNo, Double consultationFee) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DoctorProfile profile = DoctorProfile.builder()
                .user(user)
                .specialization(specialization)
                .licenseNo(licenseNo)
                .consultationFee(consultationFee)
                .build();

        return doctorProfileRepository.save(profile);
    }

    public DoctorProfile updateDoctorProfile(Long doctorId, String specialization, String licenseNo, Double consultationFee) {
        DoctorProfile profile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));

        if (specialization != null) profile.setSpecialization(specialization);
        if (licenseNo != null) profile.setLicenseNo(licenseNo);
        if (consultationFee != null) profile.setConsultationFee(consultationFee);

        return doctorProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public DoctorProfile getDoctorProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user"));
    }
}