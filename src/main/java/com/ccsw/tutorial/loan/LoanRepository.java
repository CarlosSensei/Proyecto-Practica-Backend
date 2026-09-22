package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM Loan l
        WHERE l.client.id = :clientId
        AND l.returnDate >= :loanDate
        AND l.loanDate <= :returnDate
        """)
    long countActiveLoans(Long clientId,
                          LocalDate loanDate,
                          LocalDate returnDate);

    @Query("""
        SELECT COUNT(l)
        FROM Loan l
        WHERE l.game.id = :gameId
        AND l.id <> :loanId
        AND l.returnDate >= :loanDate
        AND l.loanDate <= :returnDate
        """)
    long countOverlappingLoansExcludingId(
            Long gameId,
            Long loanId,
            LocalDate loanDate,
            LocalDate returnDate);



    @Query("""
        SELECT COUNT(l)
        FROM Loan l
        WHERE l.game.id = :gameId
        AND l.returnDate >= :loanDate
        AND l.loanDate <= :returnDate
        """)
    long countOverlappingLoans(Long gameId,
                               LocalDate loanDate,
                               LocalDate returnDate);

    @Query("""
        SELECT COUNT(l)
        FROM Loan l
        WHERE l.client.id = :clientId
        AND l.id <> :loanId
        AND l.returnDate >= :loanDate
        AND l.loanDate <= :returnDate
        """)
    long countActiveLoansExcludingId(
            Long clientId,
            Long loanId,
            LocalDate loanDate,
            LocalDate returnDate);

}
