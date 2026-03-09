package iu.devinmehringer.project1.strategy;

import java.math.BigDecimal;

public interface PriceStrategy {
    public BigDecimal nextStep(BigDecimal price);
}
