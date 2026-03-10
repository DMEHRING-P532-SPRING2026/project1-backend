package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.decorator.Notifier;
import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.ConditionType;
import iu.devinmehringer.project1.model.trade.LimitOrder;
import iu.devinmehringer.project1.model.trade.Side;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LimitOrderTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private UserService userService;

    @Mock
    private StockService stockService;

    @Mock
    private Map<String, OrderFactory> factories;

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private Notifier notifier;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private TradeUpdateService tradeUpdateService;

    private User user;
    private Stock stock;


    @BeforeEach
    void setUp() {
        user = new User(BigDecimal.valueOf(10000.00));
        stock = new Stock("AAPL", BigDecimal.valueOf(150.00));
    }

    @Test
    void limitBuyShouldTriggerWhenPriceBelowThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isTrue();
    }

    @Test
    void limitBuyShouldNotTriggerWhenPriceAboveThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(140.00), ConditionType.LESS_THAN);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isFalse();
    }

    @Test
    void limitBuyShouldTriggerWhenPriceEqualToThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(150.00), ConditionType.LESS_THAN_OR_EQUAL);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isTrue();
    }

    @Test
    void limitSellShouldTriggerWhenPriceAboveThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.SELL,
                TradeStatus.PENDING, BigDecimal.valueOf(140.00), ConditionType.GREATER_THAN);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isTrue();
    }

    @Test
    void limitSellShouldNotTriggerWhenPriceBelowThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.SELL,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.GREATER_THAN);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isFalse();
    }

    @Test
    void limitSellShouldTriggerWhenPriceEqualToThreshold() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.SELL,
                TradeStatus.PENDING, BigDecimal.valueOf(150.00), ConditionType.GREATER_THAN_OR_EQUAL);

        // Act / Assert
        assertThat(order.checkConditions(stock)).isTrue();
    }


    @Test
    void executeShouldSetPriceAndTotalPrice() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        // Act
        order.execute(BigDecimal.valueOf(150.00), BigDecimal.valueOf(1500.00));

        // Assert
        assertThat(order.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
    }

    @Test
    void executeShouldSetStatusToCompleted() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        // Act
        order.execute(BigDecimal.valueOf(150.00), BigDecimal.valueOf(1500.00));

        // Assert
        assertThat(order.getStatus()).isEqualTo(TradeStatus.COMPLETED);
    }

    @Test
    void executeShouldSetExecutedAt() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        // Act
        order.execute(BigDecimal.valueOf(150.00), BigDecimal.valueOf(1500.00));

        // Assert
        assertThat(order.getExecutedAt()).isNotNull();
    }

    @Test
    void limitBuyShouldFailWhenConditionMetButInsufficientFunds() {
        // Arrange
        LimitOrder limitOrder = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        when(tradeRepository.findByStatus(TradeStatus.PENDING)).thenReturn(List.of(limitOrder));
        when(stockService.getStockByTicker("AAPL")).thenReturn(stock);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(false);

        // Act
        tradeService.update();

        // Assert
        assertThat(limitOrder.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(userService, never()).deductFunds(any(), any());
    }
}