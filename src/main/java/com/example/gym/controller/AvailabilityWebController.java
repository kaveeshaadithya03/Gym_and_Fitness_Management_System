package com.example.gym.controller;

import com.example.gym.model.DoctorProfile;
import com.example.gym.model.TrainerProfile;
import com.example.gym.model.User;
import com.example.gym.repository.DoctorProfileRepository;
import com.example.gym.repository.TrainerProfileRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/availability")
public class AvailabilityWebController {
    
    private final AvailabilityService availabilityService;
    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    
    public AvailabilityWebController(AvailabilityService availabilityService,
                                    UserRepository userRepository,
                                    TrainerProfileRepository trainerProfileRepository,
                                    DoctorProfileRepository doctorProfileRepository) {
        this.availabilityService = availabilityService;
        this.userRepository = userRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }
    
    @GetMapping("/trainer/manage")
    public String manageTrainerAvailability(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        TrainerProfile trainer = trainerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));
        
        model.addAttribute("availabilities", 
            availabilityService.getUpcomingForTrainer(trainer.getId(), LocalDateTime.now()));
        model.addAttribute("trainerId", trainer.getId());
        return "trainer-availability";
    }
    
    @GetMapping("/doctor/manage")
    public String manageDoctorAvailability(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));
        
        model.addAttribute("availabilities", 
            availabilityService.getUpcomingForDoctor(doctor.getId(), LocalDateTime.now()));
        model.addAttribute("doctorId", doctor.getId());
        return "doctor-availability";
    }
    
    @PostMapping("/trainer/add")
    public String addTrainerAvailability(
            @RequestParam Long trainerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(required = false) String note) {
        
        availabilityService.addTrainerAvailability(trainerId, startDateTime, endDateTime, note);
        return "redirect:/availability/trainer/manage?success=added";
    }
    
    @PostMapping("/doctor/add")
    public String addDoctorAvailability(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(required = false) String note) {
        
        availabilityService.addDoctorAvailability(doctorId, startDateTime, endDateTime, note);
        return "redirect:/availability/doctor/manage?success=added";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable Long id, @RequestParam String returnTo) {
        availabilityService.deleteAvailability(id);
        return "redirect:" + returnTo + "?success=deleted";
    }
}
