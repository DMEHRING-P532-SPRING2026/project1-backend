package iu.devinmehringer.project1.model.trade;

import iu.devinmehringer.project1.model.stock.Stock;

import java.math.BigDecimal;

public interface Order {
    Trade create();
    void execute(BigDecimal price, BigDecimal totalPrice);
    boolean checkConditions(Stock stock);
}
