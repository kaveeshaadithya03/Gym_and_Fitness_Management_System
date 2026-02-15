package com.example.gym.controller;

import com.example.gym.model.DoctorProfile;
import com.example.gym.model.Role;
import com.example.gym.model.User;
import com.example.gym.model.TrainerProfile;
import com.example.gym.model.WorkoutPlan;
import com.example.gym.model.WorkoutItem;
import com.example.gym.model.MemberProfile;
import com.example.gym.model.Booking;
import com.example.gym.model.MealPlan;
import com.example.gym.repository.DoctorProfileRepository;
import com.example.gym.repository.MemberProfileRepository;
import com.example.gym.repository.TrainerProfileRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.BookingRepository;
import com.example.gym.repository.MealPlanRepository;
import com.example.gym.service.DoctorProfileService;
import com.example.gym.service.MemberChatbotService;
import com.example.gym.service.RatingService;
import com.example.gym.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorProfileService doctorProfileService;
    @Autowired
    private TrainerProfileRepository trainerProfileRepository;
    @Autowired
    private DoctorProfileRepository doctorProfileRepository;
    @Autowired
    private MemberProfileRepository memberProfileRepository;
    @Autowired
    private MemberChatbotService memberChatbotService;
    @Autowired
    private WorkoutPlanService workoutPlanService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private MealPlanRepository mealPlanRepository;

    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // Check user roles
        Set<Role> userRoles = user.getRoles();
        boolean isTrainer = userRoles.stream()
                .anyMatch(r -> Role.RoleName.ROLE_TRAINER.equals(r.getName()));
        boolean isDoctor = userRoles.stream()
                .anyMatch(r -> Role.RoleName.ROLE_DOCTOR.equals(r.getName()));

        if (isTrainer) {
            return "redirect:/trainer/dashboard";
        } else if (isDoctor) {
            return "redirect:/doctor/dashboard";
        } else {
            // For MEMBER role, check if chatbot is completed
            if (memberChatbotService.hasMemberCompletedChatbot(user.getEmail())) {
                return "redirect:/member/dashboard";
            } else {
                return "redirect:/member/chatbot-register";
            }
        }
    }

    @GetMapping("/trainer/dashboard")
    public String trainerDashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            TrainerProfile trainer = trainerProfileRepository.findByUser(user).orElse(null);
            model.addAttribute("trainer", trainer);
        } catch (Exception e) {
            // profile not found → show basic dashboard
        }
        return "trainer-dashboard";
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            DoctorProfile doctor = doctorProfileService.getDoctorProfileByUsername(username);
            model.addAttribute("doctor", doctor);
        } catch (Exception e) {
            // profile not found → show basic dashboard
        }
        return "doctor-dashboard";
    }

    @GetMapping("/member/dashboard")
    public String memberDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        // Check if user has completed chatbot registration
        boolean hasCompletedChatbot = memberChatbotService.hasMemberCompletedChatbot(user.getEmail());
        model.addAttribute("hasCompletedChatbot", hasCompletedChatbot);
        model.addAttribute("userEmail", user.getEmail());
        
        // Get member profile
        MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
        if (member != null) {
            // Calculate statistics
            long upcomingSessions = bookingRepository.findByMember(member).stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                    .count();
            
            long activeMealPlans = mealPlanRepository.findByMember(member).size();
            long workoutPlans = workoutPlanService.getWorkoutPlansForMember(member.getId()).size();
            long totalSessions = bookingRepository.findByMember(member).stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                    .count();
            
            model.addAttribute("upcomingSessions", upcomingSessions);
            model.addAttribute("activeMealPlans", activeMealPlans);
            model.addAttribute("workoutPlans", workoutPlans);
            model.addAttribute("totalSessions", totalSessions);
            
            // Get recent activity (last 5 bookings)
            List<Booking> recentBookings = bookingRepository.findByMember(member).stream()
                    .sorted((b1, b2) -> b2.getDateTime().compareTo(b1.getDateTime()))
                    .limit(5)
                    .toList();
            model.addAttribute("recentBookings", recentBookings);
        } else {
            model.addAttribute("upcomingSessions", 0L);
            model.addAttribute("activeMealPlans", 0L);
            model.addAttribute("workoutPlans", 0L);
            model.addAttribute("totalSessions", 0L);
            model.addAttribute("recentBookings", java.util.Collections.emptyList());
        }
        
        return "member-dashboard";
    }

    @GetMapping("/member/chatbot-register")
    public String memberChatbotRegister(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        String userEmail = user.getEmail();

        // Check if user has already completed chatbot
        if (memberChatbotService.hasMemberCompletedChatbot(userEmail)) {
            return "redirect:/member/dashboard";
        }

        // Check if chatbot is in use by another user
        if (memberChatbotService.isChatbotInUse()) {
            model.addAttribute("chatbotInUse", true);
            model.addAttribute("message", "The chatbot is currently being used by another member. Please try again in a few minutes.");
            return "chatbot-busy";
        }

        // Start chatbot session for this user
        if (memberChatbotService.startChatbotSession(userEmail)) {
            model.addAttribute("userEmail", userEmail);
            return "login chatbot";
        } else {
            model.addAttribute("chatbotInUse", true);
            model.addAttribute("message", "Unable to start chatbot session. Please try again later.");
            return "chatbot-busy";
        }
    }

    // Member browsing pages
    @GetMapping("/browse/trainers")
    public String browseTrainers(Model model) {
        Page<TrainerProfile> trainers = trainerProfileRepository.findAll(PageRequest.of(0, 12));
        model.addAttribute("trainers", trainers);
        return "browse-trainers";
    }

    @GetMapping("/browse/doctors")
    public String browseDoctors(Model model) {
        Page<DoctorProfile> doctors = doctorProfileRepository.findAll(PageRequest.of(0, 12));
        model.addAttribute("doctors", doctors);
        return "browse-doctors";
    }

    // Trainer workout plans management
    @GetMapping("/trainer/workout-plans")
    public String trainerManageWorkoutPlans(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            TrainerProfile trainer = trainerProfileRepository.findByUser(user).orElse(null);
            if (trainer != null) {
                // Get workout plans for this trainer
                List<WorkoutPlan> workoutPlans = workoutPlanService.getWorkoutPlansForTrainer(trainer.getId());
                List<MemberProfile> members = workoutPlanService.getAllMembers();
                model.addAttribute("workoutPlans", workoutPlans);
                model.addAttribute("members", members);
                model.addAttribute("trainerId", trainer.getId());
            } else {
                model.addAttribute("workoutPlans", java.util.Collections.emptyList());
                model.addAttribute("members", java.util.Collections.emptyList());
                model.addAttribute("trainerId", null);
            }
        } catch (Exception e) {
            model.addAttribute("workoutPlans", java.util.Collections.emptyList());
            model.addAttribute("members", java.util.Collections.emptyList());
            model.addAttribute("trainerId", null);
        }
        return "trainer-workout-plans";
    }

    // Public trainer profile view
    @GetMapping("/trainer/profile/view/{id}")
    public String viewTrainerProfile(@PathVariable Long id, Model model) {
        try {
            TrainerProfile trainer = trainerProfileRepository.findById(id).orElse(null);
            model.addAttribute("trainer", trainer);
            return "view-trainer-profile";
        } catch (Exception e) {
            return "redirect:/browse/trainers";
        }
    }

    // Public doctor profile view
    @GetMapping("/doctor/profile/view/{id}")
    public String viewDoctorProfile(@PathVariable Long id, Model model) {
        try {
            DoctorProfile doctor = doctorProfileRepository.findById(id).orElse(null);
            model.addAttribute("doctor", doctor);
            return "view-doctor-profile";
        } catch (Exception e) {
            return "redirect:/browse/doctors";
        }
    }

    // Member specific endpoints
    @GetMapping("/member/workout-plans")
    public String memberWorkoutPlans(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
                if (member != null) {
                    // Get workout plans for this member
                    List<WorkoutPlan> workoutPlans = workoutPlanService.getWorkoutPlansForMember(member.getId());
                    model.addAttribute("workoutPlans", workoutPlans);
                } else {
                    model.addAttribute("workoutPlans", java.util.Collections.emptyList());
                }
            } else {
                model.addAttribute("workoutPlans", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            model.addAttribute("workoutPlans", java.util.Collections.emptyList());
        }
        return "member-workout-plans";
    }

    @GetMapping("/member/payments")
    public String memberPayments(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
                if (member != null) {
                    // Get payments for this member
                    model.addAttribute("payments", java.util.Collections.emptyList()); // TODO: Implement service
                } else {
                    model.addAttribute("payments", java.util.Collections.emptyList());
                }
            } else {
                model.addAttribute("payments", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            model.addAttribute("payments", java.util.Collections.emptyList());
        }
        return "member-payments";
    }

    @GetMapping("/member/ratings")
    public String memberRatings(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
                if (member != null) {
                    // Get ratings by this member
                    model.addAttribute("ratings", ratingService.getRatingsByMember(member.getId()));
                } else {
                    model.addAttribute("ratings", java.util.Collections.emptyList());
                }
            } else {
                model.addAttribute("ratings", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            model.addAttribute("ratings", java.util.Collections.emptyList());
        }
        return "member-ratings";
    }
    
    @GetMapping("/workout-plans/{id}")
    public String viewWorkoutPlanDetail(@PathVariable Long id, Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            
            WorkoutPlan plan = workoutPlanService.getWorkoutPlanById(id);
            model.addAttribute("plan", plan);
            
            // Organize items by day
            java.util.Map<String, java.util.List<WorkoutItem>> itemsByDay = new java.util.LinkedHashMap<>();
            String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
            for (String day : days) {
                itemsByDay.put(day, new java.util.ArrayList<>());
            }
            for (WorkoutItem item : plan.getItems()) {
                String dayOfWeek = item.getDayOfWeek().toString();
                if (itemsByDay.containsKey(dayOfWeek)) {
                    itemsByDay.get(dayOfWeek).add(item);
                }
            }
            model.addAttribute("itemsByDay", itemsByDay);
            
            // Determine user role for navbar
            String userRole = "MEMBER";
            if (user.getRoles().stream().anyMatch(r -> r.getName().equals("TRAINER"))) {
                userRole = "TRAINER";
            } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("DOCTOR"))) {
                userRole = "DOCTOR";
            }
            model.addAttribute("userRole", userRole);
            
            return "workout-plan-detail";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }
}
