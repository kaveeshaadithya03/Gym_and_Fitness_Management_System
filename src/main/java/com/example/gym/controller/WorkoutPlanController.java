package com.example.gym.controller;

import com.example.gym.dto.WorkoutPlanRequest;
import com.example.gym.model.WorkoutItem;
import com.example.gym.model.WorkoutPlan;
import com.example.gym.service.WorkoutPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    
    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PostMapping
    public ResponseEntity<WorkoutPlan> create(@RequestBody WorkoutPlanRequest request) {
        try {
            List<WorkoutItem> workoutItems = null;
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                workoutItems = new ArrayList<>();
                for (var item : request.getItems()) {
                    WorkoutItem workoutItem = new WorkoutItem();
                    workoutItem.setExerciseName(item.getExerciseName());
                    workoutItem.setSets(item.getSets());
                    workoutItem.setReps(item.getReps());
                    workoutItem.setDayOfWeek(item.getDayOfWeek());
                    workoutItems.add(workoutItem);
                }
            }
            
            WorkoutPlan plan = workoutPlanService.createWorkoutPlan(
                    request.getMemberId(),
                    request.getTrainerId(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getDifficultyLevel(),
                    request.getDurationWeeks(),
                    workoutItems
            );
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WorkoutPlan>> getForMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlansForMember(memberId));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<WorkoutPlan>> getForTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlansForTrainer(trainerId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            workoutPlanService.deleteWorkoutPlan(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}