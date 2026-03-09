package iu.devinmehringer.project1.model.trade;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="market_orders")
public class MarketOrder extends Trade implements Order {
    protected MarketOrder() {}

    public MarketOrder(User user, String ticker, Integer quantity, Side side, TradeStatus status) {
        super(user, OrderType.MARKET, ticker, quantity, side, status);
    }

    @Override
    public String toString() {
        return "MarketOrder{}";
    }

    @Override
    public Trade create() {
        return this;
    }

    @Override
    public void execute(BigDecimal price, BigDecimal totalPrice) {
        this.setPrice(price);
        this.setTotalPrice(totalPrice);
        this.setStatus(TradeStatus.COMPLETED);
        this.setExecutedAt(LocalDateTime.now());
    }

    @Override
    public boolean checkConditions(Stock stock) {
        return true;
    }
}
