package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

        if (loanSearchDto.getGameId() != null
                && loanSearchDto.getClientId() != null) {

            return loanRepository.findByGameIdAndClientId(
                    loanSearchDto.getGameId(),
                    loanSearchDto.getClientId(),
                    loanSearchDto.getPageable().getPageable());
        }

        if (loanSearchDto.getGameId() != null) {

            return loanRepository.findByGameId(
                    loanSearchDto.getGameId(),
                    loanSearchDto.getPageable().getPageable());
        }

        if (loanSearchDto.getClientId() != null) {

            return loanRepository.findByClientId(
                    loanSearchDto.getClientId(),
                    loanSearchDto.getPageable().getPageable());
        }

        if (loanSearchDto.getLoanDate() != null) {

            return loanRepository
                    .findByLoanDateLessThanEqualAndReturnDateGreaterThanEqual(
                            loanSearchDto.getLoanDate(),
                            loanSearchDto.getLoanDate(),
                            loanSearchDto.getPageable().getPageable());
        }

        return loanRepository.findAll(
                loanSearchDto.getPageable().getPageable());
    }

    public void validateLoan(Loan loan) {

        Long id = loan.getId();
        LocalDate loanDate = loan.getLoanDate();
        LocalDate returnDate = loan.getReturnDate();

        if (loanDate == null || returnDate == null) {
            throw new IllegalArgumentException(
                    "Loan date and return date are mandatory");
        }

        if (loanDate.isAfter(returnDate)) {
            throw new IllegalArgumentException("Invalid dates: " + returnDate + " cannot be before " + loanDate);
        }

        if (returnDate.isAfter(loanDate.plusDays(14))) {
            throw new IllegalArgumentException("Cannot return the game after 14 days");
        }

        if (loanRepository.countActiveLoans(loan.getClient().getId(), id, loanDate, returnDate) >= 2) {

            throw new IllegalArgumentException("Client already has 2 active loans");
        }

        if (loanRepository.countOverlappingLoans(loan.getGame().getId(), id, loanDate, returnDate) > 0) {

            throw new IllegalArgumentException("Game already loaned in these dates");
        }

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

}
