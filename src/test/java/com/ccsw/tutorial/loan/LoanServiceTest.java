package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.Loan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void validateLoanMoreThan14DaysShouldThrowException() {

        Loan loan = new Loan();

        loan.setLoanDate(LocalDate.of(2026, 9, 1));
        loan.setReturnDate(LocalDate.of(2026, 9, 20));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> loanService.validateLoan(loan));

        assertEquals(
                "Cannot return the game after 14 days",
                exception.getMessage());
    }

    @Test
    void validateLoanWithReturnDateBeforeLoanDateShouldThrowException() {

        Loan loan = new Loan();

        loan.setLoanDate(LocalDate.of(2026, 9, 10));
        loan.setReturnDate(LocalDate.of(2026, 9, 5));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> loanService.validateLoan(loan));

        assertEquals(
                "Invalid dates: 2026-09-05 cannot be before 2026-09-10",
                exception.getMessage());
    }

    @Test
    void validateLoanShouldPass() {

        Client client = new Client();
        client.setId(1L);

        Game game = new Game();
        game.setId(1L);

        Loan loan = new Loan();

        loan.setClient(client);
        loan.setGame(game);
        loan.setLoanDate(LocalDate.of(2026, 9, 1));
        loan.setReturnDate(LocalDate.of(2026, 9, 10));

        assertDoesNotThrow(
                () -> loanService.validateLoan(loan));
    }

    @Test
    void validateLoanWithTwoActiveLoansShouldThrowException() {

        Client client = new Client();
        client.setId(1L);

        Game game = new Game();
        game.setId(6L);

        Loan loan = new Loan();

        loan.setClient(client);
        loan.setGame(game);
        loan.setLoanDate(LocalDate.of(2026, 9, 8));
        loan.setReturnDate(LocalDate.of(2026, 9, 12));

        when(loanRepository.countActiveLoans(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(2L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> loanService.validateLoan(loan));

        assertEquals(
                "Client already has 2 active loans",
                exception.getMessage());
    }

    @Test
    void validateLoanWithGameAlreadyLoanedShouldThrowException() {

        Client client = new Client();
        client.setId(1L);

        Game game = new Game();
        game.setId(1L);

        Loan loan = new Loan();

        loan.setClient(client);
        loan.setGame(game);
        loan.setLoanDate(LocalDate.of(2026, 9, 5));
        loan.setReturnDate(LocalDate.of(2026, 9, 8));

        when(loanRepository.countOverlappingLoans(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(1L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> loanService.validateLoan(loan));

        assertEquals(
                "Game already loaned in these dates",
                exception.getMessage());
    }

}
