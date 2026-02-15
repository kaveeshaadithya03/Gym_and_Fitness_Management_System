package com.example.gym.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberProfile member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id")
    private TrainerProfile trainer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    public enum BookingType {
        TRAINING, DOCTOR
    }

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    // Manual getters and setters to handle Lombok issues
    public Long getId() { return id; }
    public MemberProfile getMember() { return member; }
    public TrainerProfile getTrainer() { return trainer; }
    public DoctorProfile getDoctor() { return doctor; }
    public LocalDateTime getDateTime() { return dateTime; }
    public BookingType getType() { return type; }
    public BookingStatus getStatus() { return status; }
    
    public void setMember(MemberProfile member) { this.member = member; }
    public void setTrainer(TrainerProfile trainer) { this.trainer = trainer; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setType(BookingType type) { this.type = type; }
    public void setStatus(BookingStatus status) { this.status = status; }

    // Builder pattern methods to handle Lombok issues
    public static BookingBuilder builder() { return new BookingBuilder(); }

    public static class BookingBuilder {
        private MemberProfile member;
        private TrainerProfile trainer;
        private DoctorProfile doctor;
        private LocalDateTime dateTime;
        private BookingType type;
        private BookingStatus status = BookingStatus.PENDING;

        public BookingBuilder member(MemberProfile member) { this.member = member; return this; }
        public BookingBuilder trainer(TrainerProfile trainer) { this.trainer = trainer; return this; }
        public BookingBuilder doctor(DoctorProfile doctor) { this.doctor = doctor; return this; }
        public BookingBuilder dateTime(LocalDateTime dateTime) { this.dateTime = dateTime; return this; }
        public BookingBuilder type(BookingType type) { this.type = type; return this; }
        public BookingBuilder status(BookingStatus status) { this.status = status; return this; }

        public Booking build() {
            Booking booking = new Booking();
            booking.member = this.member;
            booking.trainer = this.trainer;
            booking.doctor = this.doctor;
            booking.dateTime = this.dateTime;
            booking.type = this.type;
            booking.status = this.status;
            return booking;
        }
    }
}