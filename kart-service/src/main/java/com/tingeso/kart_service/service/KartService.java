package com.tingeso.kart_service.service;

import com.tingeso.kart_service.DTOs.KartDTO;
import com.tingeso.kart_service.entity.Kart;
import com.tingeso.kart_service.repository.KartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KartService {
    private final KartRepository kartRepository;

    public Kart registerKart(String kartCode, String model, boolean underMaintenance) {
        if (kartRepository.existsByKartCode(kartCode)) {
            throw new IllegalArgumentException("Kart with code " + kartCode + " already exists");
        }

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setUnderMaintenance(underMaintenance);

        return kartRepository.save(kart);
    }

    public List<Kart> getAllKarts() {
        return kartRepository.findAll();
    }

    public List<Kart> getAvailableKarts() {
        return kartRepository.findAvailableKartsOnlyByMaintenance();
    }

    public Kart updateMaintenanceStatus(String kartCode, boolean underMaintenance) {
        Kart kart = findKartByCode(kartCode);
        kart.setUnderMaintenance(underMaintenance);
        return kartRepository.save(kart);
    }


    public Kart releaseKart(String kartCode) {
        Kart kart = findKartByCode(kartCode);
        return kartRepository.save(kart);
    }


    private Kart findKartByCode(String kartCode) {
        return kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found with code: " + kartCode));
    }

    public List<KartDTO> getAllKartsWithRentals() {
        return kartRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<KartDTO> getAvailableKartsWithRentals() {
        return kartRepository.findAvailableKartsOnlyByMaintenance().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KartDTO updateMaintenanceStatusWithRentalCount(String kartCode, boolean underMaintenance) {
        Kart kart = findKartByCode(kartCode);
        kart.setUnderMaintenance(underMaintenance);

        // Actualiza la fecha solo cuando se envía a mantención
        if (underMaintenance) {
            kart.setLastMaintenance(LocalDate.now());
        }

        return convertToDTO(kartRepository.save(kart));
    }

    private KartDTO convertToDTO(Kart kart) {
        KartDTO dto = new KartDTO();
        dto.setKartCode(kart.getKartCode());
        dto.setModel(kart.getModel());
        dto.setUnderMaintenance(kart.isUnderMaintenance());
        dto.setLastMaintenance(kart.getLastMaintenance());
        dto.setDescription(kart.getDescription());
        return dto;
    }
}

