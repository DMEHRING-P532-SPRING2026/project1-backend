package iu.devinmehringer.project1.model.user;

import iu.devinmehringer.project1.model.StockHolding.StockHolding;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StockHolding> holdings = new ArrayList<>();

    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean dashboardEnabled;

    public User() {}

    public User(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPortfolioValue() {
        return holdings.stream().map(holdings -> holdings.getStock().getCurrentPrice().
                multiply(BigDecimal.valueOf(holdings.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<StockHolding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<StockHolding> holdings) {
        this.holdings = holdings;
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
