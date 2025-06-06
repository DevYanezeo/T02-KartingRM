package com.tingeso.rack_service.controller;

import com.tingeso.rack_service.entity.Rack;
import com.tingeso.rack_service.service.RackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/rack")
@RequiredArgsConstructor
public class RackController {
    private final RackService rackService;

    @GetMapping("/occupied")
    public ResponseEntity<List<Rack>> getOverlappingBlocks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(rackService.getOverlappingBlocks(date, startTime, endTime));
    }

    @PostMapping("/assign")
    public ResponseEntity<Rack> assignRackBlock(@RequestBody Rack rack) {
        return ResponseEntity.ok(rackService.saveRackBlock(rack));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Rack>> getRackByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rackService.getRackByDate(date));
    }
}
