package com.example.gym.service;

import com.example.gym.model.Availability;
import com.example.gym.model.DoctorProfile;
import com.example.gym.model.TrainerProfile;
import com.example.gym.repository.AvailabilityRepository;
import com.example.gym.repository.DoctorProfileRepository;
import com.example.gym.repository.TrainerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
                              TrainerProfileRepository trainerProfileRepository,
                              DoctorProfileRepository doctorProfileRepository) {
        this.availabilityRepository = availabilityRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    public Availability addTrainerAvailability(Long trainerId, LocalDateTime start, LocalDateTime end, String note) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        
        Availability availability = new Availability();
        availability.setTrainer(trainer);
        availability.setStartDateTime(start);
        availability.setEndDateTime(end);
        availability.setNote(note);
        availability.setIsBooked(false);
        
        return availabilityRepository.save(availability);
    }
    
    public Availability addDoctorAvailability(Long doctorId, LocalDateTime start, LocalDateTime end, String note) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        Availability availability = new Availability();
        availability.setDoctor(doctor);
        availability.setStartDateTime(start);
        availability.setEndDateTime(end);
        availability.setNote(note);
        availability.setIsBooked(false);
        
        return availabilityRepository.save(availability);
    }

    @Transactional(readOnly = true)
    public List<Availability> getUpcomingForTrainer(Long trainerId, LocalDateTime from) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        List<Availability> availabilities = availabilityRepository
                .findByTrainerAndStartDateTimeAfterOrderByStartDateTimeAsc(trainer, from);
        // Eager load
        availabilities.forEach(a -> a.getTrainer().getUser().getFirstName());
        return availabilities;
    }
    
    @Transactional(readOnly = true)
    public List<Availability> getUpcomingForDoctor(Long doctorId, LocalDateTime from) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        List<Availability> availabilities = availabilityRepository
                .findByDoctorAndStartDateTimeAfterOrderByStartDateTimeAsc(doctor, from);
        // Eager load
        availabilities.forEach(a -> a.getDoctor().getUser().getFirstName());
        return availabilities;
    }
    
    @Transactional(readOnly = true)
    public List<Availability> getAvailableForTrainer(Long trainerId, LocalDateTime from) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        List<Availability> availabilities = availabilityRepository
                .findByTrainerAndIsBookedFalseAndStartDateTimeAfterOrderByStartDateTimeAsc(trainer, from);
        // Eager load
        availabilities.forEach(a -> a.getTrainer().getUser().getFirstName());
        return availabilities;
    }
    
    @Transactional(readOnly = true)
    public List<Availability> getAvailableForDoctor(Long doctorId, LocalDateTime from) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        List<Availability> availabilities = availabilityRepository
                .findByDoctorAndIsBookedFalseAndStartDateTimeAfterOrderByStartDateTimeAsc(doctor, from);
        // Eager load
        availabilities.forEach(a -> a.getDoctor().getUser().getFirstName());
        return availabilities;
    }

    @Transactional(readOnly = true)
    public List<Availability> findBetween(LocalDateTime from, LocalDateTime to) {
        return availabilityRepository.findByStartDateTimeBetweenOrderByStartDateTimeAsc(from, to);
    }
    
    public void markAsBooked(Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));
        availability.setIsBooked(true);
        availabilityRepository.save(availability);
    }
    
    public void deleteAvailability(Long availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }
}