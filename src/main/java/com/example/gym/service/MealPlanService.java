package com.example.gym.service;

import com.example.gym.model.*;
import com.example.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MealPlanService {

    @Autowired
    private MealPlanRepository mealPlanRepository;
    @Autowired
    private MealItemRepository mealItemRepository;
    @Autowired
    private MemberProfileRepository memberProfileRepository;
    @Autowired
    private DoctorProfileRepository doctorProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    public MealPlan createMealPlan(Long memberId, Long doctorId, String title, String description, Integer caloriesPerDay) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        MealPlan plan = new MealPlan();
        plan.setMember(member);
        plan.setDoctor(doctor);
        plan.setTitle(title);
        plan.setDescription(description);
        plan.setCaloriesPerDay(caloriesPerDay);

        MealPlan saved = mealPlanRepository.save(plan);
        
        // Notify member about new meal plan
        try {
            notificationService.createNotification(
                member.getUser(),
                "Dr. " + doctor.getUser().getFirstName() + " has created a new meal plan for you: " + title
            );
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        return saved;
    }

    public MealItem addMealItem(Long planId, String mealType, String description, String timeOfDay) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        MealItem item = new MealItem();
        item.setPlan(plan);
        item.setMealType(mealType);
        item.setDescription(description);
        item.setTimeOfDay(timeOfDay);

        mealItemRepository.save(item);
        plan.getItems().add(item);
        mealPlanRepository.save(plan);

        return item;
    }

    @Transactional(readOnly = true)
    public List<MealPlan> getMealPlansForMember(Long memberId) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        List<MealPlan> plans = mealPlanRepository.findByMember(member);
        
        // Eagerly load doctor, user, and items to avoid LazyInitializationException
        for (MealPlan plan : plans) {
            if (plan.getDoctor() != null) {
                plan.getDoctor().getUser().getUsername(); // Force load
            }
            plan.getItems().size(); // Force load items collection
        }
        
        return plans;
    }

    @Transactional(readOnly = true)
    public List<MealPlan> getMealPlansByDoctor(Long doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        List<MealPlan> plans = mealPlanRepository.findByDoctor(doctor);
        
        // Eagerly load member, user, and items to avoid LazyInitializationException
        for (MealPlan plan : plans) {
            if (plan.getMember() != null) {
                plan.getMember().getUser().getUsername(); // Force load
            }
            plan.getItems().size(); // Force load items collection
        }
        
        return plans;
    }

    @Transactional(readOnly = true)
    public MealPlan getMealPlan(Long id) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));
        
        // Eagerly load lazy relationships to avoid LazyInitializationException
        if (plan.getDoctor() != null) {
            plan.getDoctor().getUser().getUsername(); // Force load
        }
        if (plan.getMember() != null) {
            plan.getMember().getUser().getUsername(); // Force load
        }
        // Force load items collection
        plan.getItems().size();
        
        return plan;
    }

    public Long deleteMealPlan(Long id) {
        MealPlan plan = getMealPlan(id);
        Long doctorId = plan.getDoctor().getId();
        mealPlanRepository.delete(plan);
        return doctorId;
    }

    public Long getMemberIdByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
            if (member == null) {
                throw new IllegalArgumentException("Member profile not found for user: " + username);
            }
            return member.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error finding member: " + e.getMessage());
        }
    }

    public Long getDoctorIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user: " + username));
        return doctor.getId();
    }

    @Transactional(readOnly = true)
    public List<MemberProfile> getAllMembers() {
        return memberProfileRepository.findAll();
    }
}
