package com.example.gym.service;

import com.example.gym.dto.BookingRequest;
import com.example.gym.model.*;
import com.example.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private MemberProfileRepository memberProfileRepository;
    @Autowired
    private TrainerProfileRepository trainerProfileRepository;
    @Autowired
    private DoctorProfileRepository doctorProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PaymentService paymentService;

    @Transactional(readOnly = true)
    public List<Booking> getUpcomingBookingsForMember(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            MemberProfile member = memberProfileRepository.findByUser(user).orElse(null);
            if (member == null) {
                return List.of(); // Return empty list if no member profile
            }
            return bookingRepository.findByMemberAndDateTimeAfterOrderByDateTimeAsc(member, LocalDateTime.now());
        } catch (Exception e) {
            return List.of(); // Return empty list on any error
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getUpcomingBookingsForTrainer(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            TrainerProfile trainer = trainerProfileRepository.findByUser(user).orElse(null);
            if (trainer == null) {
                return List.of(); // Return empty list if no trainer profile
            }
            return bookingRepository.findByTrainerAndDateTimeAfterOrderByDateTimeAsc(trainer, LocalDateTime.now());
        } catch (Exception e) {
            return List.of(); // Return empty list on any error
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getUpcomingBookingsForDoctor(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user: " + username));
        return bookingRepository.findByDoctorAndDateTimeAfterOrderByDateTimeAsc(doctor, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    public Booking createBookingForMember(String username, BookingRequest request) {
        System.out.println("=== BookingService.createBookingForMember ===");
        System.out.println("Username: " + username);
        System.out.println("Request: " + request);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        System.out.println("User found: " + user.getId());
        
        MemberProfile member = memberProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Member profile not found for user: " + username));
        System.out.println("Member found: " + member.getId());

        TrainerProfile trainer = request.getTrainerId() != null ?
                trainerProfileRepository.findById(request.getTrainerId()).orElse(null) : null;
        System.out.println("Trainer: " + (trainer != null ? trainer.getId() : "null"));

        DoctorProfile doctor = request.getDoctorId() != null ?
                doctorProfileRepository.findById(request.getDoctorId()).orElse(null) : null;
        System.out.println("Doctor: " + (doctor != null ? doctor.getId() : "null"));
        
        System.out.println("DateTime: " + request.getDateTime());
        System.out.println("Type: " + request.getType());

        Booking booking = Booking.builder()
                .member(member)
                .trainer(trainer)
                .doctor(doctor)
                .dateTime(request.getDateTime())
                .type(request.getType())
                .status(Booking.BookingStatus.PENDING)
                .build();

        System.out.println("Booking object created, saving...");
        Booking saved = bookingRepository.save(booking);
        System.out.println("Booking saved with ID: " + saved.getId());
        
        return saved;
    }
    public Booking createBooking(Long memberId, Long trainerId, Long doctorId,
                                 LocalDateTime dateTime, Booking.BookingType bookingType) {

        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        TrainerProfile trainer = trainerId != null
                ? trainerProfileRepository.findById(trainerId).orElse(null)
                : null;

        DoctorProfile doctor = doctorId != null
                ? doctorProfileRepository.findById(doctorId).orElse(null)
                : null;

        Booking booking = Booking.builder()
                .member(member)
                .trainer(trainer)
                .doctor(doctor)
                .dateTime(dateTime)
                .type(bookingType)
                .status(Booking.BookingStatus.PENDING)
                .build();

        return bookingRepository.save(booking);
    }
    public Booking confirmBooking(Long id) {
        System.out.println("=== BookingService.confirmBooking ===");
        System.out.println("Booking ID: " + id);
        
        Booking booking = getBooking(id);
        System.out.println("Current status: " + booking.getStatus());
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        System.out.println("Status set to: " + booking.getStatus());
        
        Booking saved = bookingRepository.save(booking);
        System.out.println("Booking saved. New status: " + saved.getStatus());

        try {
            // Notify member
            String professionalName = booking.getTrainer() != null ? 
                booking.getTrainer().getUser().getFirstName() : 
                booking.getDoctor().getUser().getFirstName();
            notificationService.createNotification(
                booking.getMember().getUser(), 
                "Your booking with " + professionalName + " has been confirmed for " + 
                booking.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
            );
            System.out.println("Notification sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        try {
            emailService.sendEmail(
                    booking.getMember().getUser().getEmail(),
                    "Booking Confirmed",
                    "Your " + booking.getType() + " booking on " + booking.getDateTime() + " is now confirmed."
            );
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return saved;
    }

    public Booking completeBooking(Long id) {
        Booking booking = getBooking(id);
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete this booking");
        }
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);

        // Record payment when booking is completed
        try {
            Double amount = calculateBookingAmount(booking);
            Payment.PaymentType paymentType = booking.getType() == Booking.BookingType.TRAINING 
                ? Payment.PaymentType.SESSION 
                : Payment.PaymentType.DOCTOR;
            
            Payment payment = paymentService.recordPayment(
                booking.getMember().getId(),
                amount,
                paymentType,
                "CARD"
            );
            
            // Link payment to booking
            payment.setBooking(booking);
            
            System.out.println("Payment recorded: ID=" + payment.getId() + ", Amount=" + amount);
        } catch (Exception e) {
            System.err.println("Failed to record payment: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            notificationService.createNotification(
                booking.getMember().getUser(), 
                "Your session has been completed! Payment has been processed. Don't forget to rate your experience."
            );
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        return saved;
    }
    
    private Double calculateBookingAmount(Booking booking) {
        // Calculate amount based on booking type
        if (booking.getType() == Booking.BookingType.TRAINING && booking.getTrainer() != null) {
            // Use trainer's hourly rate (assuming 1 hour session)
            return booking.getTrainer().getHourlyRate();
        } else if (booking.getType() == Booking.BookingType.DOCTOR && booking.getDoctor() != null) {
            // Use doctor's consultation fee
            return booking.getDoctor().getConsultationFee();
        }
        // Default amount if rates not available
        return 50.00;
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBooking(id);
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed booking");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        
        try {
            notificationService.createNotification(
                booking.getMember().getUser(), 
                "Your booking for " + booking.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")) + 
                " has been cancelled."
            );
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        return saved;
    }
}