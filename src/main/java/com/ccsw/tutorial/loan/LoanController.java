package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Tag(name = "Loan", description = "API de Loan")
@RestController
@RequestMapping(value = "/loan")
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    ModelMapper mapper;

    // Metodo para recuperar un listado paginado de @link Loan @param dto de busqueda
    @Operation(summary = "Find Page", description = "Method that returns a page of loans")
    @PostMapping
    public Page<LoanDto> findPage(@RequestBody LoanSearchDto searchDto) {

        Page<Loan> page = this.loanService.findAll(searchDto);

        return new PageImpl<>(page.getContent().stream()
                .map(e -> mapper.map(e, LoanDto.class)).collect(Collectors.toList()),
                page.getPageable(),
                page.getTotalElements());

    }

    //Metodo para crear o recuperar un @link loan @param id PK de la entidad
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Loan")
    @PutMapping("/{id}")
    public void save(@PathVariable(name = "id", required = false) Long id, @RequestBody LoanDto loanDto) {

        this.loanService.save(id, loanDto);

    }
    @PutMapping
    public void save(@RequestBody LoanDto loanDto) {
        this.loanService.save(null, loanDto);
    }

    //Metodo para borrar un @link loan @param id PK de la entidad
    @Operation(summary = "Delete", description = "Method that deletes a Loan")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id", required = false) Long id) throws Exception {

        this.loanService.delete(id);

    }

}
