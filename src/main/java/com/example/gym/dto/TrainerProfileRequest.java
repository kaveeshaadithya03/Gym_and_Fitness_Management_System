package com.example.gym.dto;

import lombok.Data;

@Data
public class TrainerProfileRequest {
    private String specialization;
    private String certifications;
    private Integer experience;
    private Double hourlyRate;

    // Manual getters and setters to handle Lombok issues
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
}