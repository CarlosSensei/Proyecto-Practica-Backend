package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    // Implemento los metodos del interface ClientService
    @Override
    public Client get(Long id) {

        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findAll() {

        return this.clientRepository.findAllByOrderByIdAsc();
    }

    @Override
    public void save(Long id, ClientDto dto) {

        Client client;

        if (id == null) {

            if(clientRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException("Client name already exists");
            }

            client = new Client();

        } else {
            client = this.get(id);

            if (client == null) {
                throw new RuntimeException("Client with id " + id + " not found");
            }

            Client existingClient = clientRepository.findByNameIgnoreCase(dto.getName());
            if (existingClient != null && !existingClient.getId().equals(id)) {
                throw new IllegalArgumentException("Client name already exists");
            }

        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    @Override
    public void delete(Long id) throws Exception {

        if(this.get(id) == null){
            throw new Exception("This client dont exists");
        }

        this.clientRepository.deleteById(id);
    }

}
