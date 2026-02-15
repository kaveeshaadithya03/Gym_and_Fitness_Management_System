package com.example.gym.repository;

import com.example.gym.model.Invoice;
import com.example.gym.model.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByMemberOrderByIssuedDateDesc(MemberProfile member);
}