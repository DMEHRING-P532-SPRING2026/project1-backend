package iu.devinmehringer.project1.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceStrategyTest {

    @Mock
    private Random random;

    @Test
    void shouldStayWithinTwoPercentBand() {
        // Arrange
        RandomWalkPrice strategy = new RandomWalkPrice(new Random(42));
        BigDecimal price = BigDecimal.valueOf(100.00);

        // Act
        for (int i = 0; i < 1000; i++) {
            BigDecimal newPrice = strategy.nextStep(price);
            BigDecimal upperBound = price.multiply(BigDecimal.valueOf(1.02)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lowerBound = price.multiply(BigDecimal.valueOf(0.98)).setScale(2, RoundingMode.HALF_UP);

            // Assert
            assertThat(newPrice).isLessThanOrEqualTo(upperBound);
            assertThat(newPrice).isGreaterThanOrEqualTo(lowerBound);

            price = newPrice;
        }
    }

    @Test
    void shouldNeverGoBelowZero() {
        // Arrange
        RandomWalkPrice strategy = new RandomWalkPrice(new Random(42));
        BigDecimal price = BigDecimal.valueOf(0.01);

        // Act
        for (int i = 0; i < 100; i++) {
            price = strategy.nextStep(price);

            // Assert
            assertThat(price).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Test
    void shouldReturnZeroWhenPriceIsZero() {
        // Arrange
        RandomWalkPrice strategy = new RandomWalkPrice(new Random() {
            @Override
            public double nextDouble() {
                return -1.0;
            }
        });
        // Act
        BigDecimal newPrice = strategy.nextStep(BigDecimal.ZERO);

        // Assert
        assertThat(newPrice).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
