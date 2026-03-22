package iu.devinmehringer.project1.strategy;

import iu.devinmehringer.project1.model.stock.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component("RandomWalk")
public class RandomWalk implements PriceStrategy {

    private final Random random;


    public RandomWalk(Random random) {
        this.random = random;
    }

    @Override
    public BigDecimal nextStep(Stock stock) {
        BigDecimal price = stock.getCurrentPrice();
        double change = (random.nextDouble() * 0.04) - 0.02;
        BigDecimal multiplier = BigDecimal.valueOf(1 + change);
        BigDecimal newPrice = price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            newPrice = BigDecimal.ZERO;
        }
        return newPrice;
    }
}
