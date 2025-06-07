package com.tingeso.report_service.controller;

import com.tingeso.report_service.dtos.IngresosPorVueltasDTO;
import com.tingeso.report_service.dtos.IngresosPorPersonasDTO;
import com.tingeso.report_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/ingresos-por-vueltas")
    public List<IngresosPorVueltasDTO> getIngresosPorVueltas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        return reportService.obtenerIngresosPorVueltas(desde, hasta);
    }

    @GetMapping("/ingresos-por-personas")
    public List<IngresosPorPersonasDTO> getIngresosPorPersonas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        return reportService.obtenerIngresosPorPersonas(desde, hasta);
    }
} 