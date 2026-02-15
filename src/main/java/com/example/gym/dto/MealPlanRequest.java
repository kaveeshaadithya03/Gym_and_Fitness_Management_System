package com.example.gym.dto;

import lombok.Data;

@Data
public class MealPlanRequest {
    private Long memberId;
    private Long doctorId;
    private String title;
    private String description;
    private Integer caloriesPerDay;

    // Manual getters and setters to handle Lombok issues
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getCaloriesPerDay() { return caloriesPerDay; }
    public void setCaloriesPerDay(Integer caloriesPerDay) { this.caloriesPerDay = caloriesPerDay; }
}