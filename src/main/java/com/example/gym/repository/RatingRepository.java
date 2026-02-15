package com.example.gym.repository;

import com.example.gym.model.DoctorProfile;
import com.example.gym.model.MemberProfile;
import com.example.gym.model.Rating;
import com.example.gym.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByTrainer(TrainerProfile trainer);
    List<Rating> findByDoctor(DoctorProfile doctor);
    List<Rating> findByMember(MemberProfile member);

    Optional<Rating> findByMemberAndTrainer(MemberProfile member, TrainerProfile trainer);
    Optional<Rating> findByMemberAndDoctor(MemberProfile member, DoctorProfile doctor);

    @Query("SELECT COALESCE(AVG(r.score), 0) FROM Rating r WHERE r.trainer = :trainer")
    Double getAverageRatingForTrainer(@Param("trainer") TrainerProfile trainer);

    @Query("SELECT COALESCE(AVG(r.score), 0) FROM Rating r WHERE r.doctor = :doctor")
    Double getAverageRatingForDoctor(@Param("doctor") DoctorProfile doctor);
}