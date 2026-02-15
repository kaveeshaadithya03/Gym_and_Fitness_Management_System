package com.example.gym.repository;

import com.example.gym.model.Availability;
import com.example.gym.model.DoctorProfile;
import com.example.gym.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByTrainerAndStartDateTimeAfterOrderByStartDateTimeAsc(
            TrainerProfile trainer, LocalDateTime from);
    
    List<Availability> findByDoctorAndStartDateTimeAfterOrderByStartDateTimeAsc(
            DoctorProfile doctor, LocalDateTime from);

    List<Availability> findByStartDateTimeBetweenOrderByStartDateTimeAsc(
            LocalDateTime from, LocalDateTime to);
    
    List<Availability> findByTrainerAndIsBookedFalseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            TrainerProfile trainer, LocalDateTime from);
    
    List<Availability> findByDoctorAndIsBookedFalseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            DoctorProfile doctor, LocalDateTime from);
}