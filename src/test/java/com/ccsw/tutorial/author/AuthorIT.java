package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorIT {

    public static final String SERVICE_PATH = "/author";

    public static final Long DELETE_AUTHOR_ID = 6L;
    public static final Long MODIFY_AUTHOR_ID = 3L;
    public static final String NEW_AUTHOR_NAME = "Nuevo Autor";
    public static final String NEW_NATIONALITY = "Nueva Nacionalidad";

    private static final int TOTAL_AUTHORS = 6;
    private static final int PAGE_SIZE = 5;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    private final ParameterizedTypeReference<ResponsePage<AuthorDto>> responseTypePage =
            new ParameterizedTypeReference<>() {
            };

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponsePage<AuthorDto> response = restClient.post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .body(responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getTotalElements());
        assertEquals(PAGE_SIZE, response.getContent().size());
    }

    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastResult() {

        int elementsCount = TOTAL_AUTHORS - PAGE_SIZE;

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponsePage<AuthorDto> response = restClient.post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .body(responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getTotalElements());
        assertEquals(elementsCount, response.getContent().size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewAuthor() {

        long newAuthorId = TOTAL_AUTHORS + 1;
        long newAuthorSize = TOTAL_AUTHORS + 1;

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        dto.setNationality(NEW_NATIONALITY);

        restClient.put()
                .uri(SERVICE_PATH)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, (int) newAuthorSize));

        ResponsePage<AuthorDto> response = restClient.post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .body(responseTypePage);

        assertNotNull(response);
        assertEquals(newAuthorSize, response.getTotalElements());

        AuthorDto author = response.getContent()
                .stream()
                .filter(item -> item.getId().equals(newAuthorId))
                .findFirst()
                .orElse(null);

        assertNotNull(author);
        assertEquals(NEW_AUTHOR_NAME, author.getName());
    }

    @Test
    public void modifyWithExistIdShouldModifyAuthor() {

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        dto.setNationality(NEW_NATIONALITY);

        restClient.put()
                .uri(SERVICE_PATH + "/" + MODIFY_AUTHOR_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_AUTHORS));

        ResponsePage<AuthorDto> response = restClient.post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .body(responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getTotalElements());

        AuthorDto author = response.getContent()
                .stream()
                .filter(item -> item.getId().equals(MODIFY_AUTHOR_ID))
                .findFirst()
                .orElse(null);

        assertNotNull(author);
        assertEquals(NEW_AUTHOR_NAME, author.getName());
        assertEquals(NEW_NATIONALITY, author.getNationality());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {

        long authorId = TOTAL_AUTHORS + 1;

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);

        assertThrows(HttpServerErrorException.class, () ->
                restClient.put()
                        .uri(SERVICE_PATH + "/" + authorId)
                        .body(dto)
                        .retrieve()
                        .toBodilessEntity());
    }

    @Test
    public void deleteWithExistsIdShouldDeleteAuthor() {

        long newAuthorsSize = TOTAL_AUTHORS - 1;

        restClient.delete()
                .uri(SERVICE_PATH + "/" + DELETE_AUTHOR_ID)
                .retrieve()
                .toBodilessEntity();

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_AUTHORS));

        ResponsePage<AuthorDto> response = restClient.post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .body(responseTypePage);

        assertNotNull(response);
        assertEquals(newAuthorsSize, response.getTotalElements());

        AuthorDto deletedAuthor = response.getContent()
                .stream()
                .filter(author -> author.getId().equals(DELETE_AUTHOR_ID))
                .findFirst()
                .orElse(null);

        assertNull(deletedAuthor);
    }

    @Test
    public void findAllShouldReturnAllAuthor() {

        List<AuthorDto> response = restClient.get()
                .uri(SERVICE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuthorDto>>() {});

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.size());
    }


}