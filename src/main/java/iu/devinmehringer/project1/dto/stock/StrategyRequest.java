package iu.devinmehringer.project1.dto.stock;

import iu.devinmehringer.project1.model.stock.PriceStrategyType;

public class StrategyRequest {
    private PriceStrategyType strategy;

    public PriceStrategyType getStrategy() {
        return strategy;
    }

    public void setStrategy(PriceStrategyType strategy) {
        this.strategy = strategy;
    }
}