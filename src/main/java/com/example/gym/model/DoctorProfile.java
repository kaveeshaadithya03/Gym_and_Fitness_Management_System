package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String specialization;

    @Column(name = "license_no")
    private String licenseNo;

    private Double consultationFee;

    @Column(columnDefinition = "TEXT")
    private String consultationNotes;

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
    public String getSpecialization() { return specialization; }
    public String getLicenseNo() { return licenseNo; }
    public Double getConsultationFee() { return consultationFee; }
    public String getConsultationNotes() { return consultationNotes; }
    public Double getOverallRating() { return overallRating; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
}