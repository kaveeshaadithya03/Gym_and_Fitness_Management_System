package com.example.gym.controller;

import com.example.gym.dto.TrainerProfileRequest;
import com.example.gym.model.TrainerProfile;
import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.TrainerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trainer")
public class TrainerProfileController {

    private final TrainerProfileService trainerProfileService;
    private final UserRepository userRepository;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            TrainerProfile profile = trainerProfileService.getTrainerProfileByUsername(currentUsername());
            model.addAttribute("profile", profile);
            model.addAttribute("updateRequest", new TrainerProfileRequest());
            return "trainer-profile";
        } catch (IllegalArgumentException e) {
            // Profile doesn't exist, redirect to create
            return "redirect:/trainer/profile/create";
        }
    }

    @GetMapping("/profile/create")
    public String showCreateProfileForm(Model model) {
        model.addAttribute("profileRequest", new TrainerProfileRequest());
        return "trainer-profile-create";
    }

    @PostMapping("/profile/create")
    public String createProfile(@ModelAttribute TrainerProfileRequest request) {
        try {
            // Check if profile already exists
            trainerProfileService.getTrainerProfileByUsername(currentUsername());
            return "redirect:/trainer/profile";
        } catch (IllegalArgumentException e) {
            // Profile doesn't exist, create it
            User user = userRepository.findByUsername(currentUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            trainerProfileService.createTrainerProfile(
                    user.getId(),
                    request.getSpecialization(),
                    request.getCertifications(),
                    request.getExperience(),
                    request.getHourlyRate()
            );
            return "redirect:/trainer/dashboard?created=success";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute TrainerProfileRequest request) {
        TrainerProfile profile = trainerProfileService.getTrainerProfileByUsername(currentUsername());
        trainerProfileService.updateTrainerProfile(
                profile.getId(),
                request.getSpecialization(),
                request.getCertifications(),
                request.getExperience(),
                request.getHourlyRate()
        );
        return "redirect:/trainer/profile?updated=success";
    }
}