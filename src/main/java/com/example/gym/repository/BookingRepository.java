package com.example.gym.repository;

import com.example.gym.model.Booking;
import com.example.gym.model.DoctorProfile;
import com.example.gym.model.MemberProfile;
import com.example.gym.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByMember(MemberProfile member);
    
    List<Booking> findByMemberAndDateTimeAfterOrderByDateTimeAsc(
            MemberProfile member, LocalDateTime from);

    List<Booking> findByTrainerAndDateTimeAfterOrderByDateTimeAsc(
            TrainerProfile trainer, LocalDateTime from);

    List<Booking> findByDoctorAndDateTimeAfterOrderByDateTimeAsc(
            DoctorProfile doctor, LocalDateTime from);
}