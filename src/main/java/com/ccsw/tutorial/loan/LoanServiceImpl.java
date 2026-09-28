package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final GameRepository gameRepository;

    public LoanServiceImpl(LoanRepository loanRepository,
            ClientRepository clientRepository,
            GameRepository gameRepository) {

        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.gameRepository = gameRepository;
    }


    @Override
    public Page<Loan> findAll(LoanSearchDto loanSearchDto) {

        LoanSpecification gameSpec =
                new LoanSpecification(new SearchCriteria("game.id", ":", loanSearchDto.getGameId()));

        LoanSpecification clientSpec =
                new LoanSpecification(new SearchCriteria("client.id", ":", loanSearchDto.getClientId()));

        Specification<Loan> specification = Specification.where(gameSpec).and(clientSpec);

        if (loanSearchDto.getLoanDate() != null) {

            Specification<Loan> loanDateSpec =
                    new LoanSpecification(new SearchCriteria("loanDate","<=", loanSearchDto.getLoanDate()));

            Specification<Loan> returnDateSpec =
                    new LoanSpecification(new SearchCriteria("returnDate",">=", loanSearchDto.getLoanDate()));

            specification = specification.and(loanDateSpec).and(returnDateSpec);
        }

        return loanRepository.findAll(specification, loanSearchDto.getPageable().getPageable());
    }

    public void validateLoan(Loan loan) {

        LocalDate loanDate = loan.getLoanDate();
        LocalDate returnDate = loan.getReturnDate();

        validateDates(loanDate, returnDate);

        validateMaxDuration(loanDate, returnDate);

        validateClientLoanLimit(loan);

        validateGameAvailability(loan);

    }

    @Override
    public void save(Long id, LoanDto data) {

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        }

        loan.setGame(gameRepository.findById(data.getGame().getId()).orElseThrow(() -> new EntityNotFoundException("Game not found")));

        loan.setClient(clientRepository.findById(data.getClient().getId()).orElseThrow(() -> new EntityNotFoundException("Client not found")));

        loan.setLoanDate(data.getLoanDate());
        loan.setReturnDate(data.getReturnDate());

        validateLoan(loan);

        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (!loanRepository.existsById(id)) {
            throw new EntityNotFoundException("Loan not found");
        }

        loanRepository.deleteById(id);
    }

    private void validateDates(LocalDate loanDate, LocalDate returnDate) {

        if (loanDate == null || returnDate == null) {

            throw new IllegalArgumentException("Loan date and return are mandatory");
        }

        if (loanDate.isAfter(returnDate)) {

            throw new IllegalArgumentException("Invalid dates: " + returnDate + " cannot be before " + loanDate);
        }

    }

    private void validateMaxDuration(LocalDate loanDate, LocalDate returnDate) {

        if (loanDate.isAfter(returnDate.plusDays(14))) {

            throw new IllegalArgumentException("Cannot return the game after 14 days");
        }
    }

    private void validateClientLoanLimit(Loan loan) {

        List<Loan> activeLoans = loanRepository.findByClientId(loan.getClient().getId());

        long activeLoansCount = activeLoans.stream().filter(existing -> !existing.getId().equals(loan.getId()))
                .filter(existing -> overlap(loan.getLoanDate(),
                        loan.getReturnDate(), existing.getLoanDate(), existing.getReturnDate()))
                .count();

        if (activeLoansCount >= 2) {

            throw new IllegalArgumentException("Client already has 2 active loans");
        }
    }

    private void validateGameAvailability(Loan loan) {

        List<Loan> gameLoans = loanRepository.findByGameId(loan.getGame().getId());

        boolean overlapExists = gameLoans.stream().filter(existing -> !existing.getId().equals(loan.getId()))
                .anyMatch(existing -> overlap(loan.getLoanDate(), loan.getReturnDate(),
                        existing.getLoanDate(), existing.getReturnDate()));

        if (overlapExists) {

            throw new IllegalArgumentException("Game already loaned in these dates");
        }
    }

    private boolean overlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {

        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }

}
