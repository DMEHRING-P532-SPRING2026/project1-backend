package iu.devinmehringer.project1.model.trade;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="limit_orders")
public class LimitOrder extends Trade implements Order {
    private BigDecimal limitPrice;
    @Enumerated(EnumType.STRING)
    private ConditionType type;

    protected LimitOrder() {}

    public LimitOrder(User user, String ticker, Integer quantity, Side side, TradeStatus status, BigDecimal limitPrice,
                      ConditionType type) {
        super(user, OrderType.LIMIT, ticker, quantity, side, status);
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

    @Override
    public void execute(BigDecimal price, BigDecimal totalPrice) {
        this.setPrice(price);
        this.setTotalPrice(totalPrice);
        this.setStatus(TradeStatus.COMPLETED);
        this.setExecutedAt(LocalDateTime.now());
    }

    public boolean checkConditions(Stock stock) {
        return switch (this.getType()) {
            case LESS_THAN -> stock.getCurrentPrice().compareTo(getLimitPrice()) < 0;
            case LESS_THAN_OR_EQUAL -> stock.getCurrentPrice().compareTo(getLimitPrice()) <= 0;
            case GREATER_THAN -> stock.getCurrentPrice().compareTo(getLimitPrice()) > 0;
            case GREATER_THAN_OR_EQUAL -> stock.getCurrentPrice().compareTo(getLimitPrice()) >= 0;
        };
    }
}
