package com.example.gym.controller;

import com.example.gym.dto.DoctorProfileRequest;
import com.example.gym.model.DoctorProfile;
import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;
    private final UserRepository userRepository;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            DoctorProfile profile = doctorProfileService.getDoctorProfileByUsername(currentUsername());
            model.addAttribute("profile", profile);
            model.addAttribute("updateRequest", new DoctorProfileRequest());
            return "doctor-profile";
        } catch (IllegalArgumentException e) {
            // Profile doesn't exist, redirect to create
            return "redirect:/doctor/profile/create";
        }
    }

    @GetMapping("/profile/create")
    public String showCreateProfileForm(Model model) {
        model.addAttribute("profileRequest", new DoctorProfileRequest());
        return "doctor-profile-create";
    }

    @PostMapping("/profile/create")
    public String createProfile(@ModelAttribute DoctorProfileRequest request) {
        try {
            // Check if profile already exists
            doctorProfileService.getDoctorProfileByUsername(currentUsername());
            return "redirect:/doctor/profile";
        } catch (IllegalArgumentException e) {
            // Profile doesn't exist, create it
            User user = userRepository.findByUsername(currentUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            doctorProfileService.createDoctorProfile(
                    user.getId(),
                    request.getSpecialization(),
                    request.getLicenseNo(),
                    request.getConsultationFee()
            );
            return "redirect:/doctor/dashboard?created=success";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute DoctorProfileRequest request) {
        DoctorProfile profile = doctorProfileService.getDoctorProfileByUsername(currentUsername());
        doctorProfileService.updateDoctorProfile(
                profile.getId(),
                request.getSpecialization(),
                request.getLicenseNo(),
                request.getConsultationFee()
        );
        return "redirect:/doctor/profile?updated=success";
    }
}