package com.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {

    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private Long activeAccounts;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(BigDecimal totalBalance, BigDecimal monthlyIncome, BigDecimal monthlyExpenses, Long activeAccounts) {
        this.totalBalance = totalBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpenses = monthlyExpenses;
        this.activeAccounts = activeAccounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public Long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(Long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }
}
