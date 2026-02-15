package com.example.gym.service;

import com.example.gym.model.MemberProfile;
import com.example.gym.model.TrainerProfile;
import com.example.gym.model.WorkoutItem;
import com.example.gym.model.WorkoutPlan;
import com.example.gym.repository.MemberProfileRepository;
import com.example.gym.repository.TrainerProfileRepository;
import com.example.gym.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    
    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                             MemberProfileRepository memberProfileRepository,
                             TrainerProfileRepository trainerProfileRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.trainerProfileRepository = trainerProfileRepository;
    }

    public WorkoutPlan createWorkoutPlan(Long memberId, Long trainerId, String title, String description, 
                                         String difficultyLevel, Integer durationWeeks, List<WorkoutItem> items) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        WorkoutPlan plan = new WorkoutPlan();
        plan.setMember(member);
        plan.setTrainer(trainer);
        plan.setTitle(title);
        plan.setDescription(description);
        plan.setDifficultyLevel(difficultyLevel);
        plan.setDurationWeeks(durationWeeks);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setItems(new ArrayList<>()); // Initialize with empty mutable list

        WorkoutPlan savedPlan = workoutPlanRepository.save(plan);

        // Link items to plan and add them
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                item.setPlan(savedPlan);
                savedPlan.getItems().add(item);
            });
            workoutPlanRepository.save(savedPlan);
        }

        return savedPlan;
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlan> getWorkoutPlansForMember(Long memberId) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        List<WorkoutPlan> plans = workoutPlanRepository.findByMember(member);
        // Eager load trainer and user data to prevent LazyInitializationException
        plans.forEach(plan -> {
            plan.getTrainer().getUser().getUsername();
            plan.getTrainer().getUser().getEmail();
        });
        return plans;
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlan> getWorkoutPlansForTrainer(Long trainerId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        List<WorkoutPlan> plans = workoutPlanRepository.findByTrainer(trainer);
        // Eager load member and user data to prevent LazyInitializationException
        plans.forEach(plan -> {
            plan.getMember().getUser().getUsername();
            plan.getMember().getUser().getEmail();
        });
        return plans;
    }

    @Transactional(readOnly = true)
    public List<MemberProfile> getAllMembers() {
        List<MemberProfile> members = memberProfileRepository.findAll();
        // Eager load user data to prevent LazyInitializationException
        members.forEach(member -> {
            member.getUser().getFirstName();
            member.getUser().getLastName();
            member.getUser().getEmail();
        });
        return members;
    }

    @Transactional(readOnly = true)
    public WorkoutPlan getWorkoutPlanById(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout plan not found"));
        // Eager load relationships
        plan.getMember().getUser().getUsername();
        plan.getTrainer().getUser().getUsername();
        plan.getItems().size(); // Force load items
        return plan;
    }
    
    public void deleteWorkoutPlan(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout plan not found"));
        workoutPlanRepository.delete(plan);
    }

}