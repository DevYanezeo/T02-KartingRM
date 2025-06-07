package com.tingeso.kart_service.controller;

import com.tingeso.kart_service.DTOs.KartDTO;
import com.tingeso.kart_service.service.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kart")
public class KartController {

    @Autowired
    private KartService kartServices;

    @GetMapping
    public ResponseEntity<List<KartDTO>> getAllKarts() {
        return ResponseEntity.ok(kartServices.getAllKartsWithRentals());
    }

    @GetMapping("/available")
    public ResponseEntity<List<KartDTO>> getAvailableKarts() {
        return ResponseEntity.ok(kartServices.getAvailableKartsWithRentals());
    }

    @PutMapping("/{kartCode}/maintenance")
    public ResponseEntity<KartDTO> updateMaintenanceStatus(
            @PathVariable String kartCode,
            @RequestParam boolean underMaintenance) {
        return ResponseEntity.ok(
                kartServices.updateMaintenanceStatusWithRentalCount(kartCode, underMaintenance)
        );
    }

}