package iu.devinmehringer.project1.dto.stock;

import java.math.BigDecimal;

public class StockResponse {
    private String ticker;
    private BigDecimal price;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
