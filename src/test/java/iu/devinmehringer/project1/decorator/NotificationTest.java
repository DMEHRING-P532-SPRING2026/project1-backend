package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.MarketOrder;
import iu.devinmehringer.project1.model.trade.Side;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import iu.devinmehringer.project1.service.StockService;
import iu.devinmehringer.project1.service.TradeService;
import iu.devinmehringer.project1.service.UserService;
import iu.devinmehringer.project1.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceNotificationTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private UserService userService;
    @Mock
    private StockService stockService;
    @Mock
    private Map<String, OrderFactory> factories;
    @Mock
    private Notifier notifier;
    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private TradeService tradeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(BigDecimal.valueOf(10000.00));
        Stock stock = new Stock("AAPL", BigDecimal.valueOf(150.00));
        lenient().when(stockService.getStockByTicker("AAPL")).thenReturn(stock);
    }

    @Test
    void shouldNotifyWhenBuyExecuted() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(notifier).notify(contains("BUY executed"));
    }

    @Test
    void shouldNotifyWhenSellExecuted() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(true);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(notifier).notify(contains("SELL executed"));
    }

    @Test
    void shouldNotifyWhenInsufficientFunds() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(false);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(notifier).notify(contains("Insufficient funds"));
    }

    @Test
    void shouldNotifyWhenInsufficientHoldings() {
        // Arrange
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(false);

        // Act
        tradeService.executeTrade(order);

        // Assert
        verify(notifier).notify(contains("Insufficient holdings"));
    }
}