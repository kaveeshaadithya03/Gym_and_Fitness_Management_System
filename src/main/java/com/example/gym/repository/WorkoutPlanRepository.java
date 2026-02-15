package com.example.gym.repository;

import com.example.gym.model.MemberProfile;
import com.example.gym.model.TrainerProfile;
import com.example.gym.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByMember(MemberProfile member);
    List<WorkoutPlan> findByTrainer(TrainerProfile trainer);
}