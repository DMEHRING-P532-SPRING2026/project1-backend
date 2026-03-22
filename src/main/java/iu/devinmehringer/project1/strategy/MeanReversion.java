package iu.devinmehringer.project1.strategy;

import iu.devinmehringer.project1.model.stock.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Deque;
import java.util.Random;

@Component("MeanReversion")
public class MeanReversion implements PriceStrategy {
    private static final BigDecimal REVERSION_STRENGTH = new BigDecimal("0.1");
    private static final BigDecimal VOLATILITY = new BigDecimal("0.01");
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private final Random random;

    public MeanReversion(Random random) {
        this.random = random;
    }

    // newPrice = currentPrice + (REVERSION_STRENGTH × (mean - currentPrice)) + noise
    @Override
    public BigDecimal nextStep(Stock stock) {
        BigDecimal currentPrice = stock.getCurrentPrice();
        Deque<BigDecimal> history = stock.getPriceHistory();

        BigDecimal sum = history.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mean = sum.divide(BigDecimal.valueOf(history.size()), MC);

        BigDecimal pull = REVERSION_STRENGTH.multiply(mean.subtract(currentPrice), MC);

        BigDecimal randomFactor = BigDecimal.valueOf((random.nextDouble() * 2) - 1);
        BigDecimal noise = randomFactor.multiply(VOLATILITY, MC).multiply(currentPrice, MC);

        BigDecimal newPrice = currentPrice.add(pull).add(noise).setScale(2, RoundingMode.HALF_UP);

        if (newPrice.compareTo(MIN_PRICE) < 0) {
            newPrice = MIN_PRICE;
        }

        return newPrice;
    }
}
