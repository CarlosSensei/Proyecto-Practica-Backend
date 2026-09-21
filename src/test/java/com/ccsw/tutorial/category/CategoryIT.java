package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.CategoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/category";
    public static final Long NEW_CATEGORY_ID = 4L;
    public static final String NEW_CATEGORY_NAME = "CAT4";
    public static final Long MODIFY_CATEGORY_ID = 3L;
    public static final Long DELETE_CATEGORY_ID = 2L;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    ParameterizedTypeReference<List<CategoryDto>> responseType =
            new ParameterizedTypeReference<List<CategoryDto>>(){};

    @BeforeEach
    void setUp() {

        restClient = RestClient.builder()
                .baseUrl(LOCALHOST + port)
                .build();
    }

    @Test
    public void findAllShouldReturnAllCategories() {

        List<CategoryDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(responseType);

        assertNotNull(response);
        assertEquals(3, response.size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        restClient.put()
                .uri(SERVICE_PATH)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        List<CategoryDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(responseType);

        assertNotNull(response);
        assertEquals(4, response.size());

        CategoryDto categorySearch = response.stream()
                .filter(item -> item.getId().equals(NEW_CATEGORY_ID))
                .findFirst()
                .orElse(null);

        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    @Test
    public void modifyWithExistIdShouldModifyCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        restClient.put()
                .uri(SERVICE_PATH + "/" + MODIFY_CATEGORY_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        List<CategoryDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(responseType);

        assertNotNull(response);
        assertEquals(3, response.size());

        CategoryDto categorySearch = response.stream()
                .filter(item -> item.getId().equals(MODIFY_CATEGORY_ID))
                .findFirst()
                .orElse(null);

        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    @Test
    public void modifyWithNotExistIdShouldInternalError() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        HttpServerErrorException exception = assertThrows(
                HttpServerErrorException.class,
                () -> restClient.put()
                        .uri(SERVICE_PATH + "/" + NEW_CATEGORY_ID)
                        .body(dto)
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    public void deleteWithExistsIdShouldDeleteCategory() {

        restClient.delete()
                .uri(SERVICE_PATH + "/" + DELETE_CATEGORY_ID)
                .retrieve()
                .toBodilessEntity();

        List<CategoryDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(responseType);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    public void deleteWithNotExistsIdShouldInternalError() {

        HttpServerErrorException exception = assertThrows(
                HttpServerErrorException.class,
                () -> restClient.delete()
                        .uri(SERVICE_PATH + "/" + NEW_CATEGORY_ID)
                        .retrieve()
                        .toBodilessEntity()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

}
