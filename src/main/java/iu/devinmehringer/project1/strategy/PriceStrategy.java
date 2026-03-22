package iu.devinmehringer.project1.strategy;

import iu.devinmehringer.project1.model.stock.Stock;

import java.math.BigDecimal;

public interface PriceStrategy {
    public BigDecimal nextStep(Stock stock);
}
