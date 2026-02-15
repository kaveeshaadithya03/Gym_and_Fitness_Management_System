package com.example.gym.controller;

import com.example.gym.dto.MealItemRequest;
import com.example.gym.dto.MealPlanRequest;
import com.example.gym.model.MealItem;
import com.example.gym.model.MealPlan;
import com.example.gym.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanRestController {

    private final MealPlanService mealPlanService;

    @PostMapping
    public ResponseEntity<MealPlan> create(@RequestBody MealPlanRequest request) {
        return ResponseEntity.ok(mealPlanService.createMealPlan(
                request.getMemberId(), request.getDoctorId(),
                request.getTitle(), request.getDescription(), request.getCaloriesPerDay()
        ));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<MealItem> addItem(@PathVariable Long id, @RequestBody MealItemRequest item) {
        return ResponseEntity.ok(mealPlanService.addMealItem(
                id, item.getMealType(), item.getDescription(), item.getTimeOfDay()
        ));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<MealPlan>> getForMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(mealPlanService.getMealPlansForMember(memberId));
    }
}