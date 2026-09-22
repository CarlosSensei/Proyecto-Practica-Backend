package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanService {

    // Metodo para recuperar un listado paginado
    Page<Loan> findAll(LoanSearchDto loanSearchDto);

    // Metodo para crear o actualizar un @link loan
    void save(Long id, LoanDto loanDto);

    // Metodo para borrar un @link loan
    void delete(Long id) throws Exception;


}
