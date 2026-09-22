package com.ccsw.tutorial.loan;


import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String SERVICE_PATH = "/loan";
    public static final Long MODIFY_LOAN_ID = 1L;
    public static final Long DELETE_LOAN_ID = 3L;
    public static final Long NEW_GAME_ID = 3L;
    public static final Long NEW_CLIENT_ID = 3L;

    private static final int TOTAL_LOANS = 7;
    private static final int PAGE_SIZE = 5;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage =
            new ParameterizedTypeReference<>() {};

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private LoanRepository loanRepository;

    @BeforeEach
    public void setup() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

    }


    @Test
    void findFirstPageShouldReturnResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restClient
                .post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .toEntity(responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    void saveWithoutIdShouldCreateNewLoan() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);
        dto.setLoanDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        restClient
                .put()
                .uri(SERVICE_PATH)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS + 1));

        ResponseEntity<ResponsePage<LoanDto>> response = restClient
                .post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .toEntity(responseTypePage);

        assertEquals(TOTAL_LOANS + 1, response.getBody().getTotalElements());
    }

    @Test
    void saveLoanWithAlreadyLoanedGameShouldFail() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L); // juego ya prestado en data.sql

        ClientDto client = new ClientDto();
        client.setId(3L);

        dto.setGame(game);
        dto.setClient(client);
        dto.setLoanDate(LocalDate.of(2026, 9, 1));
        dto.setReturnDate(LocalDate.of(2026, 9, 10));

        assertThrows(HttpServerErrorException.class,
                () -> restClient
                        .put()
                        .uri(SERVICE_PATH)
                        .body(dto)
                        .retrieve()
                        .toBodilessEntity());
    }



    @Test
    void modifyWithExistIdShouldModifyLoan() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(3L);

        ClientDto client = new ClientDto();
        client.setId(3L);

        dto.setGame(game);
        dto.setClient(client);
        dto.setLoanDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        restClient
                .put()
                .uri(SERVICE_PATH + "/" + MODIFY_LOAN_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restClient
                .post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .toEntity(responseTypePage);

        LoanDto loan = response.getBody()
                .getContent()
                .stream()
                .filter(item -> item.getId().equals(MODIFY_LOAN_ID))
                .findFirst()
                .orElse(null);

        assertNotNull(loan);
        assertEquals(NEW_GAME_ID, loan.getGame().getId());
        assertEquals(NEW_CLIENT_ID, loan.getClient().getId());
    }

    @Test
    void modifyWithNotExistIdShouldInternalError() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        assertThrows(HttpServerErrorException.class, () ->
                restClient
                        .put()
                        .uri(SERVICE_PATH + "/" + (TOTAL_LOANS + 1))
                        .body(dto)
                        .retrieve()
                        .toBodilessEntity()
        );

    }

    @Test
    void deleteWithExistsIdShouldDeleteLoan() {

        restClient
                .delete()
                .uri(SERVICE_PATH + "/" + DELETE_LOAN_ID)
                .retrieve()
                .toBodilessEntity();

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restClient
                .post()
                .uri(SERVICE_PATH)
                .body(searchDto)
                .retrieve()
                .toEntity(responseTypePage);

        assertEquals(TOTAL_LOANS - 1, response.getBody().getTotalElements());
    }

    @Test
    void deleteWithNotExistsIdShouldThrowException() {

        assertThrows(HttpServerErrorException.class, () ->
                restClient
                        .delete()
                        .uri(SERVICE_PATH + "/" + (TOTAL_LOANS + 1))
                        .retrieve()
                        .toBodilessEntity()
        );
    }
    
}
