package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.controller.TradeController;
import iu.devinmehringer.project1.decorator.Notifier;
import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.exception.InvalidTradeException;
import iu.devinmehringer.project1.exception.UserNotFoundException;
import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.*;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.observer.Observer;
import iu.devinmehringer.project1.observer.Subject;
import iu.devinmehringer.project1.repository.TradeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TradeService implements Observer {
    private final TradeRepository tradeRepository;
    private final Map<String, OrderFactory> factories;
    private final UserService userService;
    private final StockService stockService;
    private final Notifier notifier;
    private final WebSocketService webSocketService;

    public TradeService(TradeRepository tradeRepository, Map<String, OrderFactory> factories,
                        UserService userService, StockService stockService, Notifier notifier,
                        WebSocketService webSocketService) {
        this.tradeRepository = tradeRepository;
        this.factories = factories;
        this.userService = userService;
        this.stockService = stockService;
        this.notifier = notifier;
        this.webSocketService = webSocketService;
    }

    public List<Trade> getAllPending() {
        return tradeRepository.findByStatus(TradeStatus.PENDING);
    }

    public List<Trade> getAllPendingByUser(User user) {
        return tradeRepository.findByStatusAndUser(TradeStatus.PENDING, user);
    }

    public List<Trade> getAllExecutedAndByUser(User user) {
        return tradeRepository.findByStatusInAndUser(List.of(TradeStatus.FAILED, TradeStatus.COMPLETED), user);
    }

    public List<Trade> getAllExecuted() {
        return tradeRepository.findByStatusIn(List.of(TradeStatus.FAILED, TradeStatus.COMPLETED));
    }

    public void validateTradeRequest(TradeRequest request) {
        if (!stockService.StockTickerExists(request.getTicker()))
            throw new InvalidTradeException("Invalid Ticker");
        if (request.getQuantity() <= 0)
            throw new InvalidTradeException("Negative or 0 quantity");
        if (request.getOrderType() == OrderType.LIMIT) {
            if (request.getLimitPrice() == null || request.getLimitPrice().compareTo(BigDecimal.ZERO) <= 0)
                throw new InvalidTradeException("Negative or 0 limit price");
            if (request.getConditionType() == null)
                throw new InvalidTradeException("Condition type is required");
        }
    }

    public void sendTradeUpdates(User user, Trade trade) {
        webSocketService.sendPendingTradeUpdate(getAllPendingByUser(trade.getUser()), trade.getUser());
        webSocketService.sendExecutedTradeUpdate(getAllExecutedAndByUser(trade.getUser()), trade.getUser());
    }

    public Trade createTrade(TradeRequest request) {
        validateTradeRequest(request);
        OrderFactory factory = OrderFactory.getFactory(request.getOrderType().name(), factories);
        User user = this.userService.getUser(request.getUserID()).orElseThrow(() ->
                new UserNotFoundException(request.getUserID()));
        Trade order = factory.createOrder(user, request);
        tradeRepository.save(order);
        if (request.getOrderType() == OrderType.MARKET) {
            executeTrade(order);
        }
        sendTradeUpdates(user, (Trade) order);
        return order;
    }

    @Override
    @Transactional
    public void update() {
        List<Trade> pendingTrades = tradeRepository.findByStatus(TradeStatus.PENDING);
        for (Trade trade : pendingTrades) {
            if (trade instanceof LimitOrder limitOrder) {
                Stock stock = stockService.getStockByTicker(trade.getTicker());
                if (stock == null) {
                    trade.setStatus(TradeStatus.FAILED);
                    tradeRepository.save(trade);
                    continue;
                }
                if (limitOrder.checkConditions(stock)) {
                    executeTrade(trade);
                }
            }
        }
    }

    public void executeTrade(Trade trade) {
        Stock stock = stockService.getStockByTicker(trade.getTicker());
        if (stock == null) {
            throw new InvalidTradeException("Invalid stock");
        }
        if (trade.getSide() == Side.BUY) {
           buyTrade((Order)trade, stock);
        } else {
            sellTrade((Order)trade, stock);
        }
        userService.sendUserUpdate(trade.getUser());
        sendTradeUpdates(trade.getUser(), trade);
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
