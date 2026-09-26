package com.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountCreateDto {

    @NotBlank(message = "Account holder name is required")
    private String holderName;

    @NotBlank(message = "Account type is required (e.g. Savings, Checking, Business)")
    private String accountType;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    public AccountCreateDto() {
    }

    public AccountCreateDto(String holderName, String accountType, BigDecimal initialDeposit) {
        this.holderName = holderName;
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
}
