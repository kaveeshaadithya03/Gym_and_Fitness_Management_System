package com.example.gym.dto;

import lombok.Data;

@Data
public class MealItemRequest {
    private String mealType;      // e.g. Breakfast, Lunch, Snack
    private String description;
    private String timeOfDay;     // e.g. 08:00 AM

    // Manual getters and setters to handle Lombok issues
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
}