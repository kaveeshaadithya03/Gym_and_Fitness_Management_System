package com.example.gym.controller;

import com.example.gym.model.MemberProfile;
import com.example.gym.model.Payment;
import com.example.gym.model.User;
import com.example.gym.repository.MemberProfileRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.PaymentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final MemberProfileRepository memberProfileRepository;

    public PaymentController(PaymentService paymentService, 
                           UserRepository userRepository,
                           MemberProfileRepository memberProfileRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.memberProfileRepository = memberProfileRepository;
    }

    @PostMapping("/record")
    public String recordPayment(
            @RequestParam Long memberId,
            @RequestParam Double amount,
            @RequestParam Payment.PaymentType paymentType,
            @RequestParam(defaultValue = "CARD") String method) {

        paymentService.recordPayment(memberId, amount, paymentType, method);
        return "redirect:/payments/member?payment=success";
    }

    @GetMapping("/member")
    public String memberPayments(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        MemberProfile member = memberProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Member profile not found"));
        
        model.addAttribute("payments", paymentService.getPaymentsForMember(member.getId()));
        return "payment-history";
    }
    
    @GetMapping("/invoices")
    public String memberInvoices(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        MemberProfile member = memberProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Member profile not found"));
        
        model.addAttribute("invoices", paymentService.getInvoicesForMember(member.getId()));
        return "invoice-list";
    }
    
    @GetMapping("/invoices/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", paymentService.getInvoiceById(id));
        return "invoice-detail";
    }
}