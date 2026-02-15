package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MealPlan plan;

    @Column(nullable = false)
    private String mealType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String timeOfDay;

    // Manual setters to handle Lombok issues
    public void setPlan(MealPlan plan) { this.plan = plan; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setDescription(String description) { this.description = description; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
}