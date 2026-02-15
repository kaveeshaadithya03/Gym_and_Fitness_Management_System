package com.example.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DoctorProfileRequest {

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    private String licenseNo;

    @Positive(message = "Consultation fee must be positive")
    private Double consultationFee;

    private String consultationNotes;
}