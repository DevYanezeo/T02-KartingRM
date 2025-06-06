package com.tingeso.booking_service.repository;

import com.tingeso.booking_service.entity.Booking;
import com.tingeso.booking_service.entity.BookingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);


}

