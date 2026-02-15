package com.example.gym.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkoutPlanRequest {
    private Long memberId;
    private Long trainerId;
    private String title;
    private String description;
    private String difficultyLevel;
    private Integer durationWeeks;
    private List<WorkoutItemRequest> items;
}