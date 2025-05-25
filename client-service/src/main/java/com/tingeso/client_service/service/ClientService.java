package com.tingeso.client_service.service;

import com.tingeso.client_service.entity.Client;
import com.tingeso.client_service.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    // Crear nuevo cliente
    public Client createClient(Client client) {
        if(clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }
        if(clientRepository.existsByPhone(client.getPhone())) {
            throw new RuntimeException("Teléfono ya registrado");
        }
        return clientRepository.save(client);
    }

    // Obtener cliente por ID
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // Incrementar visitas mensuales
    public void incrementMonthlyVisits(Long clientId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            client.setMonthlyVisits(client.getMonthlyVisits() + 1);
            clientRepository.save(client);
        });
    }

    // Resetear visitas mensuales (ejecutar mensualmente)
    public void resetAllMonthlyVisits() {
        clientRepository.findAll().forEach(client -> {
            client.setMonthlyVisits(0);
            clientRepository.save(client);
        });
    }

    // Buscar cliente por email
    public Optional<Client> getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}