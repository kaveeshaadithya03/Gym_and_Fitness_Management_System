package com.example.gym.controller;

import com.example.gym.model.Availability;
import com.example.gym.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/trainer/{trainerId}")
    public ResponseEntity<Availability> addTrainerAvailability(
            @PathVariable Long trainerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(availabilityService.addTrainerAvailability(trainerId, startDateTime, endDateTime, note));
    }
    
    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<Availability> addDoctorAvailability(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(availabilityService.addDoctorAvailability(doctorId, startDateTime, endDateTime, note));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Availability>> getUpcomingForTrainer(
            @PathVariable Long trainerId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        if (from == null) from = LocalDateTime.now();
        return ResponseEntity.ok(availabilityService.getUpcomingForTrainer(trainerId, from));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Availability>> getUpcomingForDoctor(
            @PathVariable Long doctorId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        if (from == null) from = LocalDateTime.now();
        return ResponseEntity.ok(availabilityService.getUpcomingForDoctor(doctorId, from));
    }
    
    @GetMapping("/trainer/{trainerId}/available")
    public ResponseEntity<List<Availability>> getAvailableForTrainer(
            @PathVariable Long trainerId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        if (from == null) from = LocalDateTime.now();
        return ResponseEntity.ok(availabilityService.getAvailableForTrainer(trainerId, from));
    }
    
    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<List<Availability>> getAvailableForDoctor(
            @PathVariable Long doctorId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        if (from == null) from = LocalDateTime.now();
        return ResponseEntity.ok(availabilityService.getAvailableForDoctor(doctorId, from));
    }
}