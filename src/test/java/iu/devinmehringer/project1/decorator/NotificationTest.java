package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.*;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import iu.devinmehringer.project1.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceNotificationTest {

    @Mock private TradeRepository tradeRepository;
    @Mock private UserService userService;
    @Mock private Map<String, OrderFactory> factories;

    @InjectMocks
    private TradeExecutionService tradeExecutionService;

    private User user;
    private Stock stock;

    @BeforeEach
    void setUp() {
        user = new User(BigDecimal.valueOf(10000.00));
        stock = new Stock("AAPL", BigDecimal.valueOf(150.00));
    }


    @Test
    void shouldNotifyWhenBuyExecuted() {
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(true);

        tradeExecutionService.buyTrade((Order) order, stock);

        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void shouldNotifyWhenSellExecuted() {
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(true);

        tradeExecutionService.sellTrade((Order) order, stock);

        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void shouldNotifyWhenInsufficientFunds() {
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        when(userService.hasSufficientBalance(any(), any())).thenReturn(false);

        tradeExecutionService.buyTrade((Order) order, stock);

        verify(tradeRepository).save(argThat(t -> ((Trade) t).getStatus() == TradeStatus.FAILED));
    }

    @Test
    void shouldNotifyWhenInsufficientHoldings() {
        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);
        when(userService.userHasStockHoldingAndQuantity(any(), any(), any())).thenReturn(false);

        tradeExecutionService.sellTrade((Order) order, stock);

        verify(tradeRepository).save(argThat(t -> ((Trade) t).getStatus() == TradeStatus.FAILED));
    }


    @Test
    void shouldFireAllNotifiersInChain() {
        // Arrange — build a chain: Email -> SMS -> Console (mock base)
        MockNotifier base = new MockNotifier();
        Notifier chain = new EmailNotifier(new SmsNotifier(base));

        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        Trade trade = (Trade) order;

        // Act
        chain.notify(user, trade);

        // Assert
        assertThat(base.getNotifyCount()).isEqualTo(1);
        assertThat(base.getCapturedTrades().get(0)).isEqualTo(trade);
        assertThat(base.getCapturedUsers().get(0)).isEqualTo(user);
    }

    @Test
    void shouldCaptureCorrectTradeInChain() {
        // Arrange
        MockNotifier base = new MockNotifier();
        Notifier chain = new EmailNotifier(base);

        MarketOrder order = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        Trade trade = (Trade) order;

        // Act
        chain.notify(user, trade);

        // Assert
        assertThat(base.getCapturedTrades().get(0).getTicker()).isEqualTo("AAPL");
        assertThat(base.getCapturedTrades().get(0).getSide()).isEqualTo(Side.BUY);
    }
}