package iu.devinmehringer.project1.factory;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.exception.UnknownOrderTypeException;
import iu.devinmehringer.project1.model.trade.*;
import iu.devinmehringer.project1.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OrderFactoryTest {

    private Map<String, OrderFactory> factories;

    @BeforeEach
    void setUp() {
        MarketOrderFactory marketOrderFactory = new MarketOrderFactory();
        LimitOrderFactory limitOrderFactory = new LimitOrderFactory();
        factories = Map.of(
                "MARKET", marketOrderFactory,
                "LIMIT", limitOrderFactory
        );
    }

    @Test
    void testCreateMarketOrder() {
        // Arrange
        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setOrderType(OrderType.MARKET);
        tradeRequest.setTicker("AAPL");
        tradeRequest.setQuantity(10);
        tradeRequest.setSide(Side.BUY);
        User user = new User();

        // Act
        OrderFactory factory = OrderFactory.getFactory(tradeRequest.getOrderType().name(), factories);
        Trade result = factory.createOrder(user, tradeRequest);

        // Assert
        assertThat(factory).isInstanceOf(MarketOrderFactory.class);
        assertThat(result).isInstanceOf(MarketOrder.class);
    }

    @Test
    void testCreateLimitOrder() {
        // Arrange
        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setOrderType(OrderType.LIMIT);
        tradeRequest.setTicker("AAPL");
        tradeRequest.setQuantity(10);
        tradeRequest.setSide(Side.BUY);
        tradeRequest.setLimitPrice(BigDecimal.valueOf(145.00));
        tradeRequest.setConditionType(ConditionType.GREATER_THAN);
        User user = new User();

        // Act
        OrderFactory factory = OrderFactory.getFactory(tradeRequest.getOrderType().name(), factories);
        Trade result = factory.createOrder(user, tradeRequest);

        // Assert
        assertThat(factory).isInstanceOf(LimitOrderFactory.class);
        assertThat(result).isInstanceOf(LimitOrder.class);
    }

    @Test
    void testThrowExceptionForUnknownOrderType() {
        // Arrange
        // No setup need
        // Act & Assert in one since it throws an error
        assertThrows(UnknownOrderTypeException.class, () ->
                OrderFactory.getFactory("UNKNOWN", factories)
        );
    }
}