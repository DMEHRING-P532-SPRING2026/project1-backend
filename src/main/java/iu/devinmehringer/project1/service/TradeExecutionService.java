package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.decorator.Notifier;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.Order;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TradeExecutionService {

    private final TradeRepository tradeRepository;
    private final UserService userService;
    private final Notifier notifier;

    public TradeExecutionService(TradeRepository tradeRepository, UserService userService, Notifier notifier) {
        this.tradeRepository = tradeRepository;
        this.userService = userService;
        this.notifier = notifier;
    }

    @Transactional
    public void buyTrade(Order order, Stock stock) {
        Trade trade = (Trade) order;
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        if (!userService.hasSufficientBalance(trade.getUser(), totalPrice)) {
            trade.setStatus(TradeStatus.FAILED);
            trade.setExecutedAt(LocalDateTime.now());
            tradeRepository.save(trade);
            notifier.notify("Insufficient funds for BUY " + trade.getTicker() + " x" + trade.getQuantity());
            return;
        }
        order.execute(stock.getCurrentPrice(), totalPrice);
        userService.deductFunds(trade.getUser(), totalPrice);
        userService.updateStockHoldingEntry(trade.getUser(), stock, trade);
        tradeRepository.save(trade);
        notifier.notify("BUY executed: " + trade.getTicker() + " x" + trade.getQuantity() + " @ " + stock.getCurrentPrice());
    }

    @Transactional
    public void sellTrade(Order order, Stock stock) {
        Trade trade = (Trade) order;
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        if (!userService.userHasStockHoldingAndQuantity(trade.getUser(), stock, trade)) {
            trade.setStatus(TradeStatus.FAILED);
            trade.setExecutedAt(LocalDateTime.now());
            tradeRepository.save(trade);
            notifier.notify("Insufficient holdings for SELL " + trade.getTicker() + " x" + trade.getQuantity());
            return;
        }
        order.execute(stock.getCurrentPrice(), totalPrice);
        userService.addFunds(trade.getUser(), totalPrice);
        userService.updateStockHoldingEntry(trade.getUser(), stock, trade);
        tradeRepository.save(trade);
        notifier.notify("SELL executed: " + trade.getTicker() + " x" + trade.getQuantity() + " @ " + stock.getCurrentPrice());
    }
}