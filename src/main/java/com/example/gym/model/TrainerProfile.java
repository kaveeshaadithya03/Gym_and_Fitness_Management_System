package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String specialization;
    private String location;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private Double hourlyRate;
    private Double overallRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    // Manual getters to handle Lombok issues
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Double getHourlyRate() { return hourlyRate; }
    public String getSpecialization() { return specialization; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
}