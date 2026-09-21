package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "client", description = "API de client" )
@RestController
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ModelMapper mapper;

    // Metodo para recuperar todos los clientes
    @Operation(summary = "find", description = "Method that return a list of Clients")
    @GetMapping("/client")
    public ResponseEntity<List<ClientDto>> findClients() {

        List<ClientDto> dtoList = this.clientService.findAll()
                .stream()
                .map(e -> mapper.map(e, ClientDto.class))
                .toList();

        return ResponseEntity.ok(dtoList);

    }

    // Metodo para crear o actualizar un cliente
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Category")
    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.PUT)
    public void save(@PathVariable(name = "id", required = false) Long id, @RequestBody ClientDto dto) {

        this.clientService.save(id, dto);
    }

    // Metodo para borrar un cliente
    @Operation(summary = "Delete", description = "Method that deletes a Category")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) throws Exception {

        this.clientService.delete(id);
    }

}
