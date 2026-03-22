package iu.devinmehringer.project1.strategy;

import iu.devinmehringer.project1.model.stock.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class PriceStrategyTest {


    @Test
    void shouldStayWithinTwoPercentBand() {
        // Arrange
        RandomWalk strategy = new RandomWalk(new Random(42));
        Stock stock = new Stock("TEST", BigDecimal.valueOf(100.00));

        // Act & Assert
        for (int i = 0; i < 1000; i++) {
            BigDecimal previousPrice = stock.getCurrentPrice();
            BigDecimal newPrice = strategy.nextStep(stock);
            BigDecimal upperBound = previousPrice.multiply(BigDecimal.valueOf(1.02)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lowerBound = previousPrice.multiply(BigDecimal.valueOf(0.98)).setScale(2, RoundingMode.HALF_UP);
            assertThat(newPrice).isLessThanOrEqualTo(upperBound);
            assertThat(newPrice).isGreaterThanOrEqualTo(lowerBound);
            stock.setCurrentPrice(newPrice);
        }
    }

    @Test
    void shouldNeverGoBelowZero() {
        // Arrange
        RandomWalk strategy = new RandomWalk(new Random(42));
        Stock stock = new Stock("TEST", BigDecimal.valueOf(0.01));

        // Act & Assert
        for (int i = 0; i < 100; i++) {
            BigDecimal newPrice = strategy.nextStep(stock);
            stock.setCurrentPrice(newPrice);
            assertThat(newPrice).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Test
    void shouldReturnZeroWhenPriceIsZero() {
        // Arrange
        RandomWalk strategy = new RandomWalk(new Random() {
            @Override
            public double nextDouble() { return -1.0; }
        });
        Stock stock = new Stock("TEST", BigDecimal.ZERO);

        // Act
        BigDecimal newPrice = strategy.nextStep(stock);

        // Assert
        assertThat(newPrice).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void meanReversionShouldPullPriceDownWhenAboveMean() {
        // Arrange
        MeanReversion strategy = new MeanReversion(new Random(42) {
            @Override public double nextDouble() { return 0.5; }
        });
        Stock stock = new Stock("TEST", BigDecimal.valueOf(100.00));
        stock.setCurrentPrice(BigDecimal.valueOf(200.00));

        // Act
        BigDecimal newPrice = strategy.nextStep(stock);

        // Assert
        assertThat(newPrice).isLessThan(BigDecimal.valueOf(200.00));
    }

    @Test
    void meanReversionShouldPullPriceUpWhenBelowMean() {
        // Arrange
        MeanReversion strategy = new MeanReversion(new Random(42) {
            @Override public double nextDouble() { return 0.5; }
        });
        Stock stock = new Stock("TEST", BigDecimal.valueOf(100.00));
        stock.setCurrentPrice(BigDecimal.valueOf(50.00));

        // Act
        BigDecimal newPrice = strategy.nextStep(stock);

        // Assert
        assertThat(newPrice).isGreaterThan(BigDecimal.valueOf(50.00));
    }

    @Test
    void meanReversionShouldNeverGoBelowMinPrice() {
        // Arrange
        MeanReversion strategy = new MeanReversion(new Random(42));
        Stock stock = new Stock("TEST", BigDecimal.valueOf(0.01));

        // Act & Assert
        for (int i = 0; i < 100; i++) {
            BigDecimal newPrice = strategy.nextStep(stock);
            stock.setCurrentPrice(newPrice);
            assertThat(newPrice).isGreaterThanOrEqualTo(new BigDecimal("0.01"));
        }
    }


    @Test
    void trendFollowingShouldContinueUpwardMomentum() {
        // Arrange
        TrendFollowing strategy = new TrendFollowing(new Random(42) {
            @Override public double nextDouble() { return 0.5; }
        });
        Stock stock = new Stock("TEST", BigDecimal.valueOf(100.00));
        for (int i = 1; i <= 10; i++) {
            stock.setCurrentPrice(BigDecimal.valueOf(100.00 + i));
            stock.addPrice(stock.getCurrentPrice());
        }
        BigDecimal priceBeforeStep = stock.getCurrentPrice();

        // Act
        BigDecimal newPrice = strategy.nextStep(stock);

        // Assert
        assertThat(newPrice).isGreaterThan(priceBeforeStep);
    }

    @Test
    void trendFollowingShouldContinueDownwardMomentum() {
        // Arrange
        TrendFollowing strategy = new TrendFollowing(new Random(42) {
            @Override public double nextDouble() { return 0.5; }
        });
        Stock stock = new Stock("TEST", BigDecimal.valueOf(100.00));
        for (int i = 1; i <= 10; i++) {
            stock.setCurrentPrice(BigDecimal.valueOf(100.00 - i));
            stock.addPrice(stock.getCurrentPrice());
        }
        BigDecimal priceBeforeStep = stock.getCurrentPrice();

        // Act
        BigDecimal newPrice = strategy.nextStep(stock);

        // Assert
        assertThat(newPrice).isLessThan(priceBeforeStep);
    }

    @Test
    void trendFollowingShouldNeverGoBelowMinPrice() {
        // Arrange
        TrendFollowing strategy = new TrendFollowing(new Random(42));
        Stock stock = new Stock("TEST", BigDecimal.valueOf(0.01));

        // Act & Assert
        for (int i = 0; i < 100; i++) {
            BigDecimal newPrice = strategy.nextStep(stock);
            stock.setCurrentPrice(newPrice);
            assertThat(newPrice).isGreaterThanOrEqualTo(new BigDecimal("0.01"));
        }
    }
}