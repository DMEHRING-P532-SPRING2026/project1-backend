package iu.devinmehringer.project1.dto.trade;

import iu.devinmehringer.project1.model.trade.ConditionType;
import iu.devinmehringer.project1.model.trade.OrderType;
import iu.devinmehringer.project1.model.trade.Side;

import java.math.BigDecimal;

public class TradeRequest {
    private OrderType orderType;
    private String ticker;
    private Integer quantity;
    private Side side;
    private BigDecimal limitPrice;
    private ConditionType conditionType;

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }
}