package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

import java.time.LocalDate;

public class LoanSearchDto {

    private String title;

    private Long clientId;

    private Long gameId;

    private LocalDate loanDate;

    private PageableRequest pageable;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGameId() { return gameId; }

    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getLoanDate() { return loanDate; }

    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public PageableRequest getPageable() {
        return pageable;
    }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}
