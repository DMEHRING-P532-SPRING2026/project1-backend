package iu.devinmehringer.project1.dto.user;

import iu.devinmehringer.project1.dto.StockHolding.StockHoldingResponse;

import java.math.BigDecimal;
import java.util.List;

public class UserResponse {
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private List<StockHoldingResponse> stockHoldingResponses;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean dashboardEnabled;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalPortfolioValue() {
        return totalPortfolioValue;
    }

    public void setTotalPortfolioValue(BigDecimal totalPortfolioValue) {
        this.totalPortfolioValue = totalPortfolioValue;
    }

    public List<StockHoldingResponse> getStockHoldingResponses() {
        return stockHoldingResponses;
    }

    public void setStockHoldingResponses(List<StockHoldingResponse> stockHoldingResponses) {
        this.stockHoldingResponses = stockHoldingResponses;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isDashboardEnabled() {
        return dashboardEnabled;
    }

    public void setDashboardEnabled(boolean dashboardEnabled) {
        this.dashboardEnabled = dashboardEnabled;
    }
}
