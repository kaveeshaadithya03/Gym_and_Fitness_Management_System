package com.example.gym.controller;

import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public String notifications(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        model.addAttribute("notifications", notificationService.getAllNotifications(user));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "notifications";
    }
    
    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }
    
    @PostMapping("/read-all")
    public String markAllAsRead() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }
}
