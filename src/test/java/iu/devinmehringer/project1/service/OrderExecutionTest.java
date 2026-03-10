package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.MarketOrder;
import iu.devinmehringer.project1.model.trade.Side;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import iu.devinmehringer.project1.decorator.Notifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderExecutionTest {
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

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private WebSocketService webSocketService;

    private User user;
    private Stock stock;
    private Trade marketBuy;
    private Trade marketSell;


    @BeforeEach
    void setUp() {
        user = new User(BigDecimal.valueOf(10000.00));
        stock = new Stock("AAPL", BigDecimal.valueOf(150.00));
        marketBuy = new MarketOrder(user, "AAPL", 10, Side.BUY, TradeStatus.PENDING);
        marketSell = new MarketOrder(user, "AAPL", 10, Side.SELL, TradeStatus.PENDING);

        when(stockService.getStockByTicker("AAPL")).thenReturn(stock);
    }

    @Test
    void marketBuyShouldReduceCashByTotalPrice() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(true);

        // Act
        tradeService.executeTrade(marketBuy);

        // Assert
        verify(userService).deductFunds(user, totalPrice);
    }

    @Test
    void marketBuyShouldIncreaseHoldingsByQuantity() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(true);

        // Act
        tradeService.executeTrade(marketBuy);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, marketBuy);
    }

    @Test
    void marketBuyShouldFailWithInsufficientFunds() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(false);

        // Act
        tradeService.executeTrade(marketBuy);


        // Assert
        assertThat(marketBuy.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(tradeRepository).save(marketBuy);
        verify(userService, never()).deductFunds(any(), any());
    }

    @Test
    void marketSellShouldIncreaseCashByTotalPrice() {
        //Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(true);

        // Act
        tradeService.executeTrade(marketSell);

        // Assert
        verify(userService).addFunds(user, totalPrice);
    }

    @Test
    void marketSellShouldReduceHoldingsByQuantity() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(true);

        // Act
        tradeService.executeTrade(marketSell);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, marketSell);
    }

    @Test
    void marketSellShouldFailWhenNoHolding() {
        // Arrange
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(false);

        // Act
        tradeService.executeTrade(marketSell);

        // Assert
        assertThat(marketSell.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(tradeRepository).save(marketSell);
        verify(userService, never()).addFunds(any(), any());
    }
}
