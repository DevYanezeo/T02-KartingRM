package com.tingeso.rack_service.service;

import com.tingeso.rack_service.dto.KartDTO;
import com.tingeso.rack_service.entity.Rack;
import com.tingeso.rack_service.repository.RackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RackService {
    private final RackRepository rackRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<Rack> getOverlappingBlocks(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return rackRepository.findOverlappingBlocks(date, startTime, endTime);
    }

    public Rack saveRackBlock(Rack rack) {
        return rackRepository.save(rack);
    }

    public List<Rack> getRackByDate(LocalDate date) {
        return rackRepository.findByDate(date);
    }

    // Método para consultar karts activos en kart-service
    public List<KartDTO> getAvailableKarts() {
        String url = "http://KART-SERVICE/api/karts/available";
        KartDTO[] karts = restTemplate.getForObject(url, KartDTO[].class);
        return Arrays.asList(karts);
    }
}
