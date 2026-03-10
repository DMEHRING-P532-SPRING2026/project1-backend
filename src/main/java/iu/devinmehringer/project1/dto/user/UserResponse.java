package iu.devinmehringer.project1.dto.user;

import iu.devinmehringer.project1.dto.StockHolding.StockHoldingResponse;

import java.math.BigDecimal;
import java.util.List;

public class UserResponse {
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private List<StockHoldingResponse> stockHoldingResponses;

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
}
