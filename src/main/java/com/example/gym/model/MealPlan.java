package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberProfile member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer caloriesPerDay;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealItem> items = new ArrayList<>();

    // Manual getters/setters to handle Lombok issues
    public Long getId() { return id; }
    public MemberProfile getMember() { return member; }
    public DoctorProfile getDoctor() { return doctor; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getCaloriesPerDay() { return caloriesPerDay; }
    public List<MealItem> getItems() { return items; }
    
    public void setMember(MemberProfile member) { this.member = member; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCaloriesPerDay(Integer caloriesPerDay) { this.caloriesPerDay = caloriesPerDay; }
}