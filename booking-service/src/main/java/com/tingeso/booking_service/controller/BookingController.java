package com.tingeso.booking_service.controller;


import com.tingeso.booking_service.dtos.BookingDTO;
import com.tingeso.booking_service.dtos.BookingRequestDTO;
import com.tingeso.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingRequestDTO request) {
        BookingDTO booking = bookingService.createBooking(request);
        return ResponseEntity.ok(booking);
    }
}