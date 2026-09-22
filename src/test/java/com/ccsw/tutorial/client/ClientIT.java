package com.ccsw.tutorial.client;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ClientIT {

    private static final String LOCALHOST = "http://localhost:";
    private static final String SERVICE_PATH = "/client";

    public static final Long MODIFY_CLIENT_ID = 1L;
    public static final Long DELETE_CLIENT_ID = 2L;

    public static final String NEW_CLIENT_NAME = "CLI4";
    public static final Long NEW_CLIENT_ID = 999L;

    private static final ParameterizedTypeReference<List<ClientDto>> responseType =
            new ParameterizedTypeReference<>() {};

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.create(LOCALHOST + port);
    }

    @Test
    void findAllShouldReturnAllClients() {

        List<ClientDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertNotNull(response);
        assertEquals(6, response.size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewClient() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(NEW_CLIENT_NAME);

        restClient.put()
                .uri(SERVICE_PATH)
                .body(clientDto)
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<List<ClientDto>> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertNotNull(response);
        assertEquals(7, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream()
                .filter(item -> NEW_CLIENT_NAME.equals(item.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());

    }

    @Test
    public void modifyWithExistIdShouldModifyClient() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(NEW_CLIENT_NAME);

        restClient.put()
                .uri(SERVICE_PATH + "/" + MODIFY_CLIENT_ID)
                .body(clientDto)
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<List<ClientDto>> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertNotNull(response);
        assertEquals(6, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream()
                .filter(item -> item.getId().equals(MODIFY_CLIENT_ID))
                .findFirst()
                .orElse(null);

        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());

    }

    @Test
    public void modifyWithNotExistIdShouldInternalError() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        HttpServerErrorException exception =
                assertThrows(HttpServerErrorException.class, () ->
                        restClient.put()
                                .uri(SERVICE_PATH + "/" + NEW_CLIENT_ID)
                                .body(dto)
                                .retrieve()
                                .toBodilessEntity());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    public void saveWithDuplicatedNameShouldReturnError() {

        ClientDto dto = new ClientDto();
        dto.setName("Juan Rodriguez");

        assertThrows(RestClientResponseException.class, () ->
                restClient.put()
                        .uri(SERVICE_PATH)
                        .body(dto)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    @Test
    public void deleteWithExistIdShouldDeleteClient() {

        restClient.delete()
                .uri(SERVICE_PATH + "/" + DELETE_CLIENT_ID)
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<List<ClientDto>> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .toEntity(responseType);

        assertNotNull(response);
        assertEquals(5, response.getBody().size());
    }

    @Test
    public void deleteWithNotExistsIdShouldInternalError() {

        assertThrows(HttpServerErrorException.InternalServerError.class, () ->
                restClient.delete()
                        .uri(SERVICE_PATH + "/" + NEW_CLIENT_ID)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

}