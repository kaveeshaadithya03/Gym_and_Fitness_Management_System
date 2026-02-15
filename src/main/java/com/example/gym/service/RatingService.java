package com.example.gym.service;

import com.example.gym.model.*;
import com.example.gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public Rating rateTrainer(Long memberId, Long trainerId, int score, String comment) {
        // Validation
        if (score < 1 || score > 5) throw new IllegalArgumentException("Score must be 1-5");

        MemberProfile member = new MemberProfile(); member.setId(memberId); // simplified
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        Rating rating = ratingRepository.findByMemberAndTrainer(member, trainer)
                .orElse(Rating.builder().member(member).trainer(trainer).build());

        rating.setScore(score);
        rating.setComment(comment);
        rating.setCreatedAt(LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);

        // Update average
        Double avg = ratingRepository.getAverageRatingForTrainer(trainer);
        trainer.setOverallRating(avg);
        trainerProfileRepository.save(trainer);

        return saved;
    }

    public Rating rateDoctor(Long memberId, Long doctorId, int score, String comment) {
        if (score < 1 || score > 5) throw new IllegalArgumentException("Score must be 1-5");

        MemberProfile member = new MemberProfile(); member.setId(memberId);
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Rating rating = ratingRepository.findByMemberAndDoctor(member, doctor)
                .orElse(Rating.builder().member(member).doctor(doctor).build());

        rating.setScore(score);
        rating.setComment(comment);
        rating.setCreatedAt(LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);

        Double avg = ratingRepository.getAverageRatingForDoctor(doctor);
        doctor.setOverallRating(avg);
        doctorProfileRepository.save(doctor);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsForTrainer(Long trainerId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId).orElseThrow();
        List<Rating> ratings = ratingRepository.findByTrainer(trainer);
        
        // Eagerly load member and user to avoid LazyInitializationException
        for (Rating rating : ratings) {
            if (rating.getMember() != null) {
                rating.getMember().getUser().getUsername(); // Force load
            }
        }
        
        return ratings;
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForTrainer(Long trainerId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId).orElseThrow();
        return ratingRepository.getAverageRatingForTrainer(trainer);
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsForDoctor(Long doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId).orElseThrow();
        List<Rating> ratings = ratingRepository.findByDoctor(doctor);
        
        // Eagerly load member and user to avoid LazyInitializationException
        for (Rating rating : ratings) {
            if (rating.getMember() != null) {
                rating.getMember().getUser().getUsername(); // Force load
            }
        }
        
        return ratings;
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForDoctor(Long doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId).orElseThrow();
        return ratingRepository.getAverageRatingForDoctor(doctor);
    }
    
    @Transactional(readOnly = true)
    public List<Rating> getRatingsByMember(Long memberId) {
        MemberProfile member = new MemberProfile();
        member.setId(memberId);
        List<Rating> ratings = ratingRepository.findByMember(member);
        
        // Eagerly load trainer/doctor and user to avoid LazyInitializationException
        for (Rating rating : ratings) {
            if (rating.getTrainer() != null) {
                rating.getTrainer().getUser().getUsername(); // Force load
            }
            if (rating.getDoctor() != null) {
                rating.getDoctor().getUser().getUsername(); // Force load
            }
        }
        
        return ratings;
    }
}