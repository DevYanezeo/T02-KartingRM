package com.tingeso.booking_service.repository;

import com.tingeso.booking_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Métodos personalizados si los necesitas
}