package iu.devinmehringer.project1.model.trade;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="market_orders")
public class MarketOrder extends Trade implements Order {
    protected MarketOrder() {}

    public MarketOrder(String ticker, Integer quantity, Side side, TradeStatus status) {
        super(OrderType.MARKET, ticker, quantity, side, status);
    }

    @Override
    public String toString() {
        return "MarketOrder{}";
    }

    @Override
    public Trade create() {
        return this;
    }
}
