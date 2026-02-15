package com.example.gym.controller;

import com.example.gym.model.TrainerProfile;
import com.example.gym.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public List<TrainerProfile> search(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location) {
        if (specialization != null && !specialization.isBlank()) {
            return trainerService.searchBySpecialization(specialization);
        }
        if (location != null && !location.isBlank()) {
            return trainerService.searchByLocation(location);
        }
        return trainerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerProfile> get(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getTrainer(id));
    }
}