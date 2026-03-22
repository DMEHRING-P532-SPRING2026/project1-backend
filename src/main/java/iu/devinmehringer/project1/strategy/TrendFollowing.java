package iu.devinmehringer.project1.strategy;

import iu.devinmehringer.project1.model.stock.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

@Component("TrendFollowing")
public class TrendFollowing implements PriceStrategy {
    private static final BigDecimal MOMENTUM_STRENGTH = new BigDecimal("0.1");
    private static final BigDecimal VOLATILITY = new BigDecimal("0.01");
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private final Random random;

    public TrendFollowing(Random random) {
        this.random = random;
    }

    // momentum = average of all (price[i] - price[i-1]) in history
    // newPrice = currentPrice + (MOMENTUM_STRENGTH × momentum) + noise
    @Override
    public BigDecimal nextStep(Stock stock) {
        BigDecimal currentPrice = stock.getCurrentPrice();
        Deque<BigDecimal> history = stock.getPriceHistory();

        List<BigDecimal> prices = new ArrayList<>(history);
        BigDecimal totalChange = BigDecimal.ZERO;
        for (int i = 1; i < prices.size(); i++) {
            totalChange = totalChange.add(prices.get(i).subtract(prices.get(i - 1)));
        }
        BigDecimal momentum = totalChange.divide(BigDecimal.valueOf(prices.size() - 1), MC);

        BigDecimal pull = MOMENTUM_STRENGTH.multiply(momentum, MC);

        BigDecimal randomFactor = BigDecimal.valueOf((random.nextDouble() * 2) - 1);
        BigDecimal noise = randomFactor.multiply(VOLATILITY, MC).multiply(currentPrice, MC);

        BigDecimal newPrice = currentPrice.add(pull).add(noise).setScale(2, RoundingMode.HALF_UP);

        if (newPrice.compareTo(MIN_PRICE) < 0) {
            newPrice = MIN_PRICE;
        }

        return newPrice;
    }
}
