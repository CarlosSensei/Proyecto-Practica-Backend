package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByOrderByIdAsc();
    Client findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
