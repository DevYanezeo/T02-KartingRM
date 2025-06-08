package com.tingeso.kart_service.config;

import com.tingeso.kart_service.entity.Kart;
import com.tingeso.kart_service.repository.KartRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.stream.IntStream;

@Component
public class KartDataLoader implements CommandLineRunner {

    @Autowired
    private KartRepository kartRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo poblar si la tabla está vacía
        if (kartRepository.count() == 0) {
            IntStream.rangeClosed(1, 15).forEach(i -> {
                String code = String.format("K%03d", i); // K001, K002, ..., K015
                Kart kart = new Kart(
                        code,
                        "Sodikart RT8",
                        false, // underMaintenance
                        "Kart en perfecto estado",
                        LocalDate.now()
                );
                kartRepository.save(kart);
            });
            System.out.println("¡Karts iniciales insertados en la base de datos!");
        }
    }
}