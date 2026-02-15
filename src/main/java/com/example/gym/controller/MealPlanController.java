package com.example.gym.controller;

import com.example.gym.dto.MealItemRequest;
import com.example.gym.dto.MealPlanRequest;
import com.example.gym.model.MealPlan;
import com.example.gym.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/meal-plans")
public class MealPlanController {

    @Autowired
    private MealPlanService mealPlanService;

    @GetMapping("/member")
    public String memberMealPlans(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long memberId = mealPlanService.getMemberIdByUsername(username);
            model.addAttribute("mealPlans", mealPlanService.getMealPlansForMember(memberId));
        } catch (Exception e) {
            model.addAttribute("mealPlans", java.util.Collections.emptyList());
            model.addAttribute("error", "No meal plans found. Please contact a doctor to create your meal plan.");
        }
        return "member-meal-plans";
    }

    @GetMapping("/doctor/{doctorId}")
    public String doctorMealPlans(@PathVariable Long doctorId, Model model) {
        model.addAttribute("mealPlans", mealPlanService.getMealPlansByDoctor(doctorId));
        model.addAttribute("doctorId", doctorId);
        return "doctor-meal-plans";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long doctorId = mealPlanService.getDoctorIdByUsername(username);

        MealPlanRequest request = new MealPlanRequest();
        request.setDoctorId(doctorId);

        // Get list of members for selection
        model.addAttribute("members", mealPlanService.getAllMembers());
        model.addAttribute("mealPlanRequest", request);
        model.addAttribute("doctorId", doctorId);
        return "create-meal-plan";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute MealPlanRequest request) {
        MealPlan plan = mealPlanService.createMealPlan(
                request.getMemberId(), request.getDoctorId(),
                request.getTitle(), request.getDescription(), request.getCaloriesPerDay()
        );
        return "redirect:/meal-plans/" + plan.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("mealPlan", mealPlanService.getMealPlan(id));
        return "meal-plan-detail";
    }

    @PostMapping("/{id}/add-item")
    public String addItem(@PathVariable Long id, @ModelAttribute MealItemRequest item) {
        mealPlanService.addMealItem(id, item.getMealType(), item.getDescription(), item.getTimeOfDay());
        return "redirect:/meal-plans/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        Long doctorId = mealPlanService.deleteMealPlan(id);
        return "redirect:/meal-plans/doctor/" + doctorId;
    }
}
