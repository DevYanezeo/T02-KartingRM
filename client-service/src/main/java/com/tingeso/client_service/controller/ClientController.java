package com.tingeso.client_service.controller;

import com.tingeso.client_service.entity.Client;
import com.tingeso.client_service.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        try {
            return ResponseEntity.ok(clientService.createClient(client));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Optional<Client> client = clientService.getClientByEmail(email);
        return client.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/increment-visits")
    public ResponseEntity<Void> incrementVisits(@PathVariable Long id) {
        clientService.incrementMonthlyVisits(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-visits")
    public ResponseEntity<Void> resetAllVisits() {
        clientService.resetAllMonthlyVisits();
        return ResponseEntity.ok().build();
    }
}