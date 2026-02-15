package com.example.gym.controller;

import com.example.gym.dto.BookingRequest;
import com.example.gym.model.Booking;
import com.example.gym.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // Member - List bookings & create form
    @GetMapping("/member/bookings")
    public String memberBookings(Model model) {
        List<Booking> bookings = bookingService.getUpcomingBookingsForMember(currentUsername());
        model.addAttribute("bookings", bookings);
        model.addAttribute("bookingRequest", new BookingRequest());
        return "member-bookings";
    }

    @PostMapping("/member/bookings")
    public String createMemberBooking(@ModelAttribute BookingRequest request) {
        bookingService.createBookingForMember(currentUsername(), request);
        return "redirect:/member/bookings?created=success";
    }

    // Trainer view
    @GetMapping("/trainer/bookings")
    public String trainerBookings(Model model) {
        List<Booking> bookings = bookingService.getUpcomingBookingsForTrainer(currentUsername());
        model.addAttribute("bookings", bookings);
        return "trainer-bookings";
    }

    // Doctor view
    @GetMapping("/doctor/bookings")
    public String doctorBookings(Model model) {
        List<Booking> bookings = bookingService.getUpcomingBookingsForDoctor(currentUsername());
        model.addAttribute("bookings", bookings);
        return "doctor-bookings";
    }

    // Common - View single booking
    @GetMapping("/bookings/{id}")
    public String viewBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBooking(id);
        model.addAttribute("booking", booking);
        return "booking-detail";
    }

    // Create booking form (GET)
    @GetMapping("/bookings/create")
    public String createBookingForm(@RequestParam(required = false) Long trainerId, 
                                   @RequestParam(required = false) Long doctorId, Model model) {
        BookingRequest bookingRequest = new BookingRequest();
        
        // Set the type based on whether it's a trainer or doctor booking
        if (trainerId != null) {
            bookingRequest.setTrainerId(trainerId);
            bookingRequest.setType(Booking.BookingType.TRAINING);
        } else if (doctorId != null) {
            bookingRequest.setDoctorId(doctorId);
            bookingRequest.setType(Booking.BookingType.DOCTOR);
        }
        
        model.addAttribute("trainerId", trainerId);
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("bookingRequest", bookingRequest);
        return "create-booking";
    }

    // Create booking (POST)
    @PostMapping("/bookings/create")
    public String createBooking(@ModelAttribute BookingRequest request, Model model) {
        try {
            System.out.println("=== Booking Request Debug ===");
            System.out.println("TrainerId: " + request.getTrainerId());
            System.out.println("DoctorId: " + request.getDoctorId());
            System.out.println("DateTime: " + request.getDateTime());
            System.out.println("Type: " + request.getType());
            System.out.println("Username: " + currentUsername());
            
            Booking booking = bookingService.createBookingForMember(currentUsername(), request);
            System.out.println("Booking created with ID: " + booking.getId());
            
            return "redirect:/member/bookings?created=success";
        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "redirect:/bookings/create?trainerId=" + request.getTrainerId() + 
                   "&doctorId=" + request.getDoctorId() + "&error=true";
        }
    }

    // Confirm (trainer/doctor)
    @PostMapping("/bookings/{id}/confirm")
    public String confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return redirectBasedOnRole("confirmed=success");
    }

    // Complete (trainer/doctor)
    @PostMapping("/bookings/{id}/complete")
    public String completeBooking(@PathVariable Long id) {
        bookingService.completeBooking(id);
        return redirectBasedOnRole("completed=success");
    }

    // Cancel (member)
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/member/bookings?cancelled=success";
    }

    private String redirectBasedOnRole(String param) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"))) {
            return "redirect:/trainer/bookings?" + param;
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            return "redirect:/doctor/bookings?" + param;
        }
        return "redirect:/";
    }
}