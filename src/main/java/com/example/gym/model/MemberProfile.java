package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "member_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double heightCm;
    private Double weightKg;

    @Column(columnDefinition = "TEXT")
    private String goals;

    private String membershipType;
    private LocalDate membershipEndDate;

    // Manual getters to handle Lombok issues
    public Long getId() { return id; }
    public User getUser() { return user; }
}