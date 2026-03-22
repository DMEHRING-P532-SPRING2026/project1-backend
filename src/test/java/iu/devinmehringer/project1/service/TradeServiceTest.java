package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.decorator.Notifier;
import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.exception.InvalidTradeException;
import iu.devinmehringer.project1.factory.LimitOrderFactory;
import iu.devinmehringer.project1.factory.MarketOrderFactory;
import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.*;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock private TradeRepository tradeRepository;
    @Mock private UserService userService;
    @Mock private StockService stockService;
    @Mock private Map<String, OrderFactory> factories;
    @Mock private Notifier notifier;
    @Mock private TradeUpdateService tradeUpdateService;
    @Mock private WebSocketService webSocketService;

    private TradeExecutionService tradeExecutionService;
    private TradeService tradeService;

    private User user;
    private Stock stock;

    @BeforeEach
    void setUp() {
        user = new User(BigDecimal.valueOf(10000.00));
        stock = new Stock("AAPL", BigDecimal.valueOf(150.00));

        // Real TradeExecutionService so actual logic runs
        tradeExecutionService = new TradeExecutionService(tradeRepository, userService, notifier, webSocketService);
        tradeService = new TradeService(tradeRepository, factories, userService, stockService, notifier, tradeUpdateService, tradeExecutionService);

        lenient().when(stockService.getStockByTicker("AAPL")).thenReturn(stock);
        lenient().when(factories.get("MARKET")).thenReturn(new MarketOrderFactory());
        lenient().when(factories.get("LIMIT")).thenReturn(new LimitOrderFactory());
    }

    @Test
    void marketBuyShouldCreateHoldingWhenNoneExists() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, order);
    }

    @Test
    void marketBuyShouldIncreaseExistingHoldingQuantity() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, order);
    }

    @Test
    void marketSellShouldRemoveHoldingWhenQuantityReachesZero() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, order);
    }

    @Test
    void marketSellShouldReduceHoldingQuantityWhenPartialSell() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 5, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, order);
        assertThat(order.getStatus()).isEqualTo(TradeStatus.COMPLETED);
    }

    @Test
    void marketSellShouldFailWhenSellingMoreThanOwned() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(false);

        // Act
        tradeService.executeTrade(order);

        // Assert
        assertThat(order.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(userService, never()).addFunds(any(), any());
    }

    @Test
    void marketBuyShouldDeductCorrectAmountFromBalance() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        BigDecimal expectedTotal = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).deductFunds(user, expectedTotal);
    }

    @Test
    void marketSellShouldAddCorrectAmountToBalance() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        BigDecimal expectedTotal = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(userService).addFunds(user, expectedTotal);
    }

    @Test
    void limitOrderShouldNotExecuteWhenConditionNotMet() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(100.00), ConditionType.LESS_THAN);
        when(tradeRepository.findByStatus(TradeStatus.PENDING)).thenReturn(List.of(order));

        // Act
        tradeService.update();

        // Assert
        assertThat(order.getStatus()).isEqualTo(TradeStatus.PENDING);
        verify(userService, never()).deductFunds(any(), any());
    }

    @Test
    void limitOrderShouldFailWhenStockNotFound() {
        // Arrange
        LimitOrder order = new LimitOrder(user, "INVALID", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);
        when(tradeRepository.findByStatus(TradeStatus.PENDING)).thenReturn(List.of(order));
        when(stockService.getStockByTicker("INVALID")).thenReturn(null);

        // Act
        tradeService.update();

        // Assert
        assertThat(order.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(userService, never()).deductFunds(any(), any());
    }

    @Test
    void shouldFailSecondOrderWhenFirstDrainsBalance() {
        // Arrange
        user = new User(BigDecimal.valueOf(2000.00));
        LimitOrder order1 = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);
        LimitOrder order2 = new LimitOrder(user, "AAPL", 10, Side.BUY,
                TradeStatus.PENDING, BigDecimal.valueOf(160.00), ConditionType.LESS_THAN);

        when(tradeRepository.findByStatus(TradeStatus.PENDING)).thenReturn(List.of(order1, order2));
        when(userService.hasSufficientBalance(any(), any()))
                .thenReturn(true)
                .thenReturn(false);

        // Act
        tradeService.update();

        // Assert
        assertThat(order1.getStatus()).isEqualTo(TradeStatus.COMPLETED);
        assertThat(order2.getStatus()).isEqualTo(TradeStatus.FAILED);
    }

    @Test
    void createTradeShouldThrowWhenTickerInvalid() {
        // Arrange
        TradeRequest request = new TradeRequest();
        request.setOrderType(OrderType.MARKET);
        request.setTicker("INVALID");
        request.setQuantity(10);
        request.setSide(Side.BUY);
        request.setUserID(1L);
        when(stockService.StockTickerExists("INVALID")).thenReturn(false);

        // Act / Assert
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void createTradeShouldThrowWhenQuantityIsZero() {
        // Arrange
        TradeRequest request = new TradeRequest();
        request.setOrderType(OrderType.MARKET);
        request.setTicker("AAPL");
        request.setQuantity(0);
        request.setSide(Side.BUY);
        request.setUserID(1L);
        when(stockService.StockTickerExists("AAPL")).thenReturn(true);

        // Act / Assert
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void createTradeShouldThrowWhenQuantityIsNegative() {
        // Arrange
        TradeRequest request = new TradeRequest();
        request.setOrderType(OrderType.MARKET);
        request.setTicker("AAPL");
        request.setQuantity(-5);
        request.setSide(Side.BUY);
        request.setUserID(1L);
        when(stockService.StockTickerExists("AAPL")).thenReturn(true);

        // Act / Assert
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void createTradeShouldThrowWhenLimitOrderMissingConditionType() {
        // Arrange
        TradeRequest request = new TradeRequest();
        request.setOrderType(OrderType.LIMIT);
        request.setTicker("AAPL");
        request.setQuantity(10);
        request.setSide(Side.BUY);
        request.setUserID(1L);
        request.setLimitPrice(BigDecimal.valueOf(140.00));
        request.setConditionType(null);
        when(stockService.StockTickerExists("AAPL")).thenReturn(true);

        // Act / Assert
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void createTradeShouldThrowWhenLimitPriceIsNegative() {
        // Arrange
        TradeRequest request = new TradeRequest();
        request.setOrderType(OrderType.LIMIT);
        request.setTicker("AAPL");
        request.setQuantity(10);
        request.setSide(Side.BUY);
        request.setUserID(1L);
        request.setLimitPrice(BigDecimal.valueOf(-10.00));
        request.setConditionType(ConditionType.LESS_THAN);
        when(stockService.StockTickerExists("AAPL")).thenReturn(true);

        // Act / Assert
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(request));
    }
}