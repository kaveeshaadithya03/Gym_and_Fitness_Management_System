package com.example.gym.repository;

import com.example.gym.model.MemberProfile;
import com.example.gym.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMember(MemberProfile member);

    // Fixed: now takes 3 parameters (from, to, status)
    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p " +
            "WHERE p.dateTime BETWEEN :from AND :to " +
            "AND p.status = :status")
    Double getTotalRevenueForPeriod(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") Payment.PaymentStatus status
    );
}