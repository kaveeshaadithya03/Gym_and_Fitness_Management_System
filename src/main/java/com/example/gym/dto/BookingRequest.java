package com.example.gym.dto;

import com.example.gym.model.Booking;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    private Long memberId;  // Will be set by controller from authenticated user

    private Long trainerId;     // optional for doctor bookings

    private Long doctorId;      // optional for trainer bookings

    @NotNull(message = "Date & Time is required")
    @Future(message = "Booking must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;

    @NotNull(message = "Booking type is required")
    private Booking.BookingType type;

    // Manual getters and setters to handle Lombok issues
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    
    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }
    
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    
    public Booking.BookingType getType() { return type; }
    public void setType(Booking.BookingType type) { this.type = type; }
}