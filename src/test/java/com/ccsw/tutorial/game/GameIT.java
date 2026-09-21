package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.game.model.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/game";

    public static final Long EXISTS_GAME_ID = 1L;
    public static final Long NOT_EXISTS_GAME_ID = 0L;

    private static final String NOT_EXISTS_TITLE = "NotExists";
    private static final String EXISTS_TITLE = "Aventureros";
    private static final String NEW_TITLE = "Nuevo juego";

    private static final Long NOT_EXISTS_CATEGORY = 0L;
    private static final Long EXISTS_CATEGORY = 3L;

    private static final String TITLE_PARAM = "title";
    private static final String CATEGORY_ID_PARAM = "idCategory";

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.builder()
                .baseUrl(LOCALHOST + port)
                .build();
    }

    private String buildUrl(String title, Long categoryId) {

        return UriComponentsBuilder
                .fromPath(SERVICE_PATH)
                .queryParam(TITLE_PARAM, title)
                .queryParam(CATEGORY_ID_PARAM, categoryId)
                .build()
                .toUriString();
    }

    private List<GameDto> findGames(String title, Long categoryId) {

        return restClient.get()
                .uri(buildUrl(title, categoryId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<GameDto>>() {});
    }

    @Test
    public void findWithoutFiltersShouldReturnAllGamesInDB() {

        int expectedGames = 6;

        List<GameDto> response = findGames(null, null);

        assertNotNull(response);
        assertEquals(expectedGames, response.size());
    }

    @Test
    public void findExistsTitleShouldReturnGames() {

        int expectedGames = 1;

        List<GameDto> response = findGames(EXISTS_TITLE, null);

        assertNotNull(response);
        assertEquals(expectedGames, response.size());
    }

    @Test
    public void findExistsCategoryShouldReturnGames() {

        int expectedGames = 2;

        List<GameDto> response = findGames(null, EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(expectedGames, response.size());
    }

    @Test
    public void findExistsTitleAndCategoryShouldReturnGames() {

        int expectedGames = 1;

        List<GameDto> response = findGames(EXISTS_TITLE, EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(expectedGames, response.size());
    }

    @Test
    public void findNotExistsTitleShouldReturnEmpty() {

        List<GameDto> response = findGames(NOT_EXISTS_TITLE, null);

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void findNotExistsCategoryShouldReturnEmpty() {

        List<GameDto> response = findGames(null, NOT_EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void findNotExistsTitleOrCategoryShouldReturnEmpty() {

        List<GameDto> response =
                findGames(NOT_EXISTS_TITLE, NOT_EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(0, response.size());

        response =
                findGames(NOT_EXISTS_TITLE, EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(0, response.size());

        response =
                findGames(EXISTS_TITLE, NOT_EXISTS_CATEGORY);

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewGame() {

        GameDto dto = new GameDto();

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        List<GameDto> response =
                findGames(NEW_TITLE, null);

        assertNotNull(response);
        assertEquals(0, response.size());

        restClient.put()
                .uri(SERVICE_PATH)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        response =
                findGames(NEW_TITLE, null);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    public void modifyWithExistIdShouldModifyGame() {

        GameDto dto = new GameDto();

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        List<GameDto> response =
                findGames(NEW_TITLE, null);

        assertNotNull(response);
        assertEquals(0, response.size());

        restClient.put()
                .uri(SERVICE_PATH + "/" + EXISTS_GAME_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        response =
                findGames(NEW_TITLE, null);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(EXISTS_GAME_ID, response.get(0).getId());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {

        GameDto dto = new GameDto();
        dto.setTitle(NEW_TITLE);

        HttpServerErrorException exception =
                assertThrows(HttpServerErrorException.class,
                        () -> restClient.put()
                                .uri(SERVICE_PATH + "/" + NOT_EXISTS_GAME_ID)
                                .body(dto)
                                .retrieve()
                                .toBodilessEntity());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatusCode()
        );
    }
}