package com.ccsw.tutorial.client;


import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;

import java.util.List;

public interface ClientService {

    // Recupera un cliente por id
    Client get(Long id);

    // Recupera todos los clientes.
    List<Client> findAll();

    // Metodo para crear o actualizar un cliente
    void save(Long Id, ClientDto clientDto);

    // Metodo para borrar un cliente
    void delete(Long Id) throws Exception;
}
