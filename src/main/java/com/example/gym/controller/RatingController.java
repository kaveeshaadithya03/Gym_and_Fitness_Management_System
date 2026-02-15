package com.example.gym.controller;

import com.example.gym.dto.RatingRequest;
import com.example.gym.model.Booking;
import com.example.gym.model.MemberProfile;
import com.example.gym.service.BookingService;
import com.example.gym.service.MemberProfileService;
import com.example.gym.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final BookingService bookingService;
    private final MemberProfileService memberProfileService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/form/{bookingId}")
    public String showRatingForm(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBooking(bookingId);
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            return "redirect:/member/bookings?error=only_completed_bookings_can_be_rated";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("ratingRequest", new RatingRequest());
        return "rating-form";
    }

    @PostMapping("/submit")
    public String submitRating(@ModelAttribute RatingRequest request,
                               @RequestParam Long bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        MemberProfile member = memberProfileService.getMemberByUsername(currentUsername());

        if (booking.getTrainer() != null) {
            ratingService.rateTrainer(member.getId(), booking.getTrainer().getId(),
                    request.getScore(), request.getComment());
        } else if (booking.getDoctor() != null) {
            ratingService.rateDoctor(member.getId(), booking.getDoctor().getId(),
                    request.getScore(), request.getComment());
        }

        return "redirect:/member/bookings?rated=success";
    }

    @GetMapping("/trainer/{trainerId}")
    public String viewTrainerRatings(@PathVariable Long trainerId, Model model) {
        model.addAttribute("ratings", ratingService.getRatingsForTrainer(trainerId));
        model.addAttribute("avgRating", ratingService.getAverageRatingForTrainer(trainerId));
        return "trainer-ratings";
    }

    @GetMapping("/doctor/{doctorId}")
    public String viewDoctorRatings(@PathVariable Long doctorId, Model model) {
        model.addAttribute("ratings", ratingService.getRatingsForDoctor(doctorId));
        model.addAttribute("avgRating", ratingService.getAverageRatingForDoctor(doctorId));
        return "doctor-ratings";
    }
}