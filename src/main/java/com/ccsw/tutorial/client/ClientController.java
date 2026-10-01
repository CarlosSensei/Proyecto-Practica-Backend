package com.ccsw.tutorial.client;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.client.model.ClientSearchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Tag(name = "client", description = "API de client" )
@RestController
@RequestMapping("/client")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    ModelMapper mapper;

    // Metodo para recuperar pagina de clientes
    @Operation(summary = "Find Page", description = "Method to return a page of clients")
    @PostMapping
    public Page<ClientDto> findPage(@RequestBody ClientSearchDto dto) {

        Page<Client> page = this.clientService.findPage(dto);

        return new PageImpl<>(page.getContent().stream()
                .map(e -> mapper.map(e, ClientDto.class))
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    // Metodo para recuperar todos los clientes
    @Operation(summary = "find", description = "Method that return a list of Clients")
    @GetMapping
    public List<ClientDto> findAll() {

        List<Client> clients =  clientService.findAll();

        return clients.stream().map(e -> mapper.map(e, ClientDto.class)).collect(Collectors.toList());
    }

    // Metodo para crear o actualizar un cliente
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Category")
    @PutMapping({ "", "/{id}" })
    public void save(@PathVariable(required = false) Long id,
                     @RequestBody ClientDto dto) {

        this.clientService.save(id, dto);
    }

    // Metodo para borrar un cliente
    @Operation(summary = "Delete", description = "Method that deletes a Category")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) throws Exception {

        this.clientService.delete(id);
    }

}
