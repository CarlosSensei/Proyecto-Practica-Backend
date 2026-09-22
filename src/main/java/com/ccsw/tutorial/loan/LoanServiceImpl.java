package com.ccsw.tutorial.loan;


import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private GameRepository gameRepository;

    @Override
    public Page<Loan> findAll(LoanSearchDto loanSearchDto) {

        return this.loanRepository.findAll(loanSearchDto.getPageable().getPageable());

    }

    @Override
    public void save(Long id, LoanDto data) {

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new);
        }

        loan.setGame(gameRepository.findById(data.getGame().getId()).orElseThrow(() -> new RuntimeException("Game not found")));
        loan.setClient(clientRepository.findById(data.getClient().getId()).orElseThrow(() -> new RuntimeException("Client not found")));

        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.loanRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);

    }
}
