package com.example.gym.repository;

import com.example.gym.model.DoctorProfile;
import com.example.gym.model.MealPlan;
import com.example.gym.model.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByMember(MemberProfile member);
    List<MealPlan> findByDoctor(DoctorProfile doctor);
}