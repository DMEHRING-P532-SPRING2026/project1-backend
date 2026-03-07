package iu.devinmehringer.project1.model.trade;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name="limit_orders")
public class LimitOrder extends Trade implements Order {
    private BigDecimal limitPrice;
    @Enumerated(EnumType.STRING)
    private ConditionType type;

    protected LimitOrder() {}

    public LimitOrder(String ticker, Integer quantity, Side side, TradeStatus status, BigDecimal limitPrice,
                      ConditionType type) {
        super(OrderType.LIMIT, ticker, quantity, side, status);
        this.limitPrice = limitPrice;
        this.type = type;
    }

    @Override
    public String toString() {
        return "LimitOrder{" +
                "limitPrice=" + limitPrice +
                ", type=" + type +
                '}';
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    @Override
    public Trade create() {
        return this;
    }
}
