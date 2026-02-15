package com.example.gym.service;

import com.example.gym.model.Invoice;
import com.example.gym.model.MemberProfile;
import com.example.gym.model.Payment;
import com.example.gym.repository.InvoiceRepository;
import com.example.gym.repository.MemberProfileRepository;
import com.example.gym.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository, 
                         MemberProfileRepository memberProfileRepository,
                         InvoiceRepository invoiceRepository,
                         NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    public Payment recordPayment(Long memberId, Double amount, Payment.PaymentType type, String method) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setAmount(amount);
        payment.setPaymentType(type);
        payment.setMethod(method);
        payment.setDateTime(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PAID);

        Payment savedPayment = paymentRepository.save(payment);
        
        // Generate invoice
        try {
            generateInvoice(savedPayment);
        } catch (Exception e) {
            // Log but don't fail the payment
            System.err.println("Failed to generate invoice: " + e.getMessage());
        }
        
        // Send notification
        try {
            notificationService.createNotification(
                member.getUser(),
                "Payment of $" + amount + " received successfully. Invoice generated."
            );
        } catch (Exception e) {
            System.err.println("Failed to send payment notification: " + e.getMessage());
        }

        return savedPayment;
    }
    
    public Invoice generateInvoice(Payment payment) {
        String invoiceNumber = "INV-" + System.currentTimeMillis();
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setPayment(payment);
        invoice.setMember(payment.getMember());
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setTotalAmount(payment.getAmount());
        invoice.setTaxAmount(payment.getAmount() * 0.1); // 10% tax
        invoice.setDescription(getPaymentDescription(payment));
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        
        return invoiceRepository.save(invoice);
    }
    
    private String getPaymentDescription(Payment payment) {
        switch (payment.getPaymentType()) {
            case MEMBERSHIP:
                return "Gym Membership Fee";
            case SESSION:
                return "Training Session Payment";
            case DOCTOR:
                return "Doctor Consultation Fee";
            default:
                return "Payment";
        }
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForMember(Long memberId) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        List<Payment> payments = paymentRepository.findByMember(member);
        // Eager load relationships
        payments.forEach(p -> {
            p.getMember().getUser().getFirstName();
            if (p.getBooking() != null) {
                p.getBooking().getId();
            }
        });
        return payments;
    }
    
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesForMember(Long memberId) {
        MemberProfile member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        List<Invoice> invoices = invoiceRepository.findByMemberOrderByIssuedDateDesc(member);
        // Eager load relationships
        invoices.forEach(inv -> {
            inv.getMember().getUser().getFirstName();
            inv.getPayment().getAmount();
        });
        return invoices;
    }
    
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        // Eager load
        invoice.getMember().getUser().getFirstName();
        invoice.getPayment().getAmount();
        return invoice;
    }
}