package com.ccsw.tutorial.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ccsw.tutorial.client.model.*;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test de duplicados con Mockito
@ExtendWith(MockitoExtension.class)
public class ClientTest {

    public static final String CLIENT_NAME = "CLI1";
    public static final Long EXISTS_CLIENT_ID = 1L;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    // Test de devolver toda la lista de clientes
    @Test
    public void findAllShouldReturnAllClients() {

        List<Client> clients = new ArrayList<>();
        clients.add(mock(Client.class));

        when(clientRepository.findAllByOrderByIdAsc()).thenReturn(clients);

        List<Client> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(clients.size(), result.size());
    }

    // Test de save de creacion de cliente con id inexistente
    @Test
    public void saveNotExistsClientIdShouldInsert() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, clientDto);

        verify(clientRepository).save(captor.capture());

        assertEquals(CLIENT_NAME, captor.getValue().getName());
    }

    // Test de borrado de cliente existente
    @Test
    public void deleteExistsClientIdShouldDelete() throws Exception {

        Client client = mock(Client.class);

        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.delete(EXISTS_CLIENT_ID);

        verify(clientRepository).deleteById(EXISTS_CLIENT_ID);
    }

}
