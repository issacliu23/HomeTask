package com.example.homework.account;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AccountValidationRequestDTO {

    @NotNull
    private Integer accountNumber;
    private List<String> providers;

    public AccountValidationRequestDTO() {}
    public AccountValidationRequestDTO(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountValidationRequestDTO(Integer accountNumber, List<String> providers) {
        this.accountNumber = accountNumber;
        this.providers = providers;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public List<String> getProviders() {
        return providers;
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }
}
