package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private WorkoutPlan plan;

    @Column(nullable = false)
    private String exerciseName;

    private Integer sets;
    private Integer reps;
    private String dayOfWeek;
}