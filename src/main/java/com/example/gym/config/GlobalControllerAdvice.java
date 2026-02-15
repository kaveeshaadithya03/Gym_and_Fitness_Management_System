package com.example.gym.config;

import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    public GlobalControllerAdvice(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }
    
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            try {
                String username = auth.getName();
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    long unreadCount = notificationService.getUnreadCount(user);
                    model.addAttribute("unreadNotificationCount", unreadCount);
                }
            } catch (Exception e) {
                // Silently fail - notifications are not critical
                model.addAttribute("unreadNotificationCount", 0L);
            }
        }
    }
}
