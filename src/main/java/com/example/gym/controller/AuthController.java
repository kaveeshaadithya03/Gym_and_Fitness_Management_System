package com.example.gym.controller;

import com.example.gym.dto.UserRegistrationRequest;
import com.example.gym.model.Role;
import com.example.gym.model.User;
import com.example.gym.model.MemberProfile;
import com.example.gym.repository.RoleRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.MemberProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberProfileRepository memberProfileRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") UserRegistrationRequest request,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            model.addAttribute("error", "Username already taken");
            return "register";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            model.addAttribute("error", "Email already in use");
            return "register";
        }

        // Map role string to RoleName enum
        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf("ROLE_" + request.getRole());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid role selected");
            return "register";
        }

        Role userRole = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = Role.builder().name(roleName).build();
                    return roleRepository.save(newRole);
                });

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getUsername()) // Use username as firstName
                .lastName("") // Empty lastName
                .status(User.UserStatus.ACTIVE)
                .roles(Collections.singleton(userRole))
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create profile based on role
        if (roleName == Role.RoleName.ROLE_MEMBER) {
            MemberProfile memberProfile = MemberProfile.builder()
                    .user(savedUser)
                    .membershipType("BASIC")
                    .build();
            memberProfileRepository.save(memberProfile);
        }
        // Note: Doctor and Trainer profiles are created via their respective profile creation forms

        return "redirect:/login?registered";
    }
}