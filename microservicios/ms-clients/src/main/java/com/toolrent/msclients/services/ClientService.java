package com.toolrent.msclients.services;

import com.toolrent.msclients.entities.ClientEntity;
import com.toolrent.msclients.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<ClientEntity> getAllClients() {
        return clientRepository.findAll();
    }

    public ClientEntity getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    public ClientEntity getClientByRut(String rut) {
        return clientRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con RUT: " + rut));
    }

    @Transactional
    public ClientEntity saveClient(ClientEntity client) {
        validateClientData(client);

        if (client.getId() == null) {
            if (clientRepository.existsByRut(client.getRut())) {
                throw new IllegalArgumentException("Ya existe un cliente con este RUT");
            }
            if (clientRepository.existsByEmail(client.getEmail())) {
                throw new IllegalArgumentException("Ya existe un cliente con este email");
            }
        }

        if (client.getStatus() == null) {
            client.setStatus(ClientEntity.ClientStatus.ACTIVE);
        }

        return clientRepository.save(client);
    }

    @Transactional
    public ClientEntity updateClient(Long id, ClientEntity client) {
        ClientEntity existing = getClientById(id);

        existing.setName(client.getName());
        existing.setPhone(client.getPhone());
        existing.setStatus(client.getStatus());

        if (!existing.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(client.getEmail())) {
                throw new IllegalArgumentException("Ya existe un cliente con este email");
            }
            existing.setEmail(client.getEmail());
        }

        return clientRepository.save(existing);
    }

    @Transactional
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public ClientEntity restrictClient(Long id) {
        ClientEntity client = getClientById(id);
        client.setStatus(ClientEntity.ClientStatus.RESTRICTED);
        return clientRepository.save(client);
    }

    @Transactional
    public ClientEntity activateClient(Long id) {
        ClientEntity client = getClientById(id);
        client.setStatus(ClientEntity.ClientStatus.ACTIVE);
        return clientRepository.save(client);
    }

    public List<ClientEntity> searchClients(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }

    private void validateClientData(ClientEntity client) {
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es requerido");
        }
        if (client.getRut() == null || client.getRut().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT es requerido");
        }
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es requerido");
        }
    }
}

