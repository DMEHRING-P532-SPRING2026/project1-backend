package iu.devinmehringer.project1.model.stock;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

@Entity
@Table(name="stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ticker;
    private BigDecimal currentPrice;

    @Transient
    private int historyWindow = 60;

    @Transient
    private Deque<BigDecimal> priceHistory = new ArrayDeque<>();

    public Stock() {}

    public Stock(String ticker, BigDecimal currentPrice) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        for (int i = 0; i < historyWindow; i++) {
            priceHistory.addLast(currentPrice);
        }
    }

    public void addPrice(BigDecimal price) {
        if (priceHistory.size() >= historyWindow) {
            priceHistory.pollFirst();
        }
        priceHistory.addLast(price);
    }

    public void setHistoryWindow(int historyWindow) {
        this.historyWindow = historyWindow;
    }

    public int getHistoryWindow() {
        return historyWindow;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        this.addPrice(currentPrice);
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deque<BigDecimal> getPriceHistory() {
        return priceHistory;
    }
}