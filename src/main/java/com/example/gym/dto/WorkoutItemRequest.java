package com.example.gym.dto;

import lombok.Data;

@Data
public class WorkoutItemRequest {
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private String dayOfWeek;   // e.g. Monday, Tuesday...
}