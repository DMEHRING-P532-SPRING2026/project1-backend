package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.*;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderExecutionTest {
    @Mock private TradeRepository tradeRepository;
    @Mock private UserService userService;
    @Mock private Notifier notifier;

    @InjectMocks
    private TradeExecutionService tradeExecutionService;

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
    }

    @Test
    void marketBuyShouldReduceCashByTotalPrice() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(true);

        // Act
        tradeExecutionService.buyTrade((Order) marketBuy, stock);

        // Assert
        verify(userService).deductFunds(user, totalPrice);
    }

    @Test
    void marketBuyShouldIncreaseHoldingsByQuantity() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(true);

        // Act
        tradeExecutionService.buyTrade((Order) marketBuy, stock);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, marketBuy);
    }

    @Test
    void marketBuyShouldFailWithInsufficientFunds() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.hasSufficientBalance(user, totalPrice)).thenReturn(false);

        // Act
        tradeExecutionService.buyTrade((Order) marketBuy, stock);

        // Assert
        assertThat(marketBuy.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(tradeRepository).save(marketBuy);
        verify(userService, never()).deductFunds(any(), any());
    }

    @Test
    void marketSellShouldIncreaseCashByTotalPrice() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(true);

        // Act
        tradeExecutionService.sellTrade((Order) marketSell, stock);

        // Assert
        verify(userService).addFunds(user, totalPrice);
    }

    @Test
    void marketSellShouldReduceHoldingsByQuantity() {
        // Arrange
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(10));
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(true);

        // Act
        tradeExecutionService.sellTrade((Order) marketSell, stock);

        // Assert
        verify(userService).updateStockHoldingEntry(user, stock, marketSell);
    }

    @Test
    void marketSellShouldFailWhenNoHolding() {
        // Arrange
        when(userService.userHasStockHoldingAndQuantity(user, stock, marketSell)).thenReturn(false);

        // Act
        tradeExecutionService.sellTrade((Order) marketSell, stock);

        // Assert
        assertThat(marketSell.getStatus()).isEqualTo(TradeStatus.FAILED);
        verify(tradeRepository).save(marketSell);
        verify(userService, never()).addFunds(any(), any());
    }
}