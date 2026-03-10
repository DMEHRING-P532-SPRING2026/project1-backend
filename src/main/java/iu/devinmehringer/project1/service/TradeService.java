package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.decorator.Notifier;
import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.exception.InvalidTradeException;
import iu.devinmehringer.project1.exception.UserNotFoundException;
import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.*;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.observer.Observer;
import iu.devinmehringer.project1.repository.TradeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TradeService implements Observer {
    private final TradeRepository tradeRepository;
    private final Map<String, OrderFactory> factories;
    private final UserService userService;
    private final StockService stockService;
    private final Notifier notifier;
    private final TradeUpdateService tradeUpdateService;
    private final TradeExecutionService tradeExecutionService;

    public TradeService(TradeRepository tradeRepository, Map<String, OrderFactory> factories,
                        UserService userService, StockService stockService, Notifier notifier,
                        TradeUpdateService tradeUpdateService, TradeExecutionService tradeExecutionService) {
        this.tradeExecutionService = tradeExecutionService;
        this.tradeUpdateService = tradeUpdateService;
        this.tradeRepository = tradeRepository;
        this.factories = factories;
        this.userService = userService;
        this.stockService = stockService;
        this.notifier = notifier;
    }

    public List<Trade> getAllPending() {
        return tradeRepository.findByStatus(TradeStatus.PENDING);
    }

    public List<Trade> getAllPendingByUser(User user) {
        return tradeRepository.findByStatusAndUser(TradeStatus.PENDING, user);
    }

    public List<Trade> getAllPendingByUser(Long userID) {
        return userService.getUser(userID)
                .map(user -> tradeRepository.findByStatusAndUser(TradeStatus.PENDING, user))
                .orElse(List.of());
    }

    public List<Trade> getAllExecutedByUser(User user) {
        return tradeRepository.findByStatusInAndUser(List.of(TradeStatus.FAILED, TradeStatus.COMPLETED), user);
    }

    public List<Trade> getAllExecutedByUser(Long userID) {
        return userService.getUser(userID)
                .map(user -> tradeRepository.findByStatusInAndUser(List.of(TradeStatus.FAILED, TradeStatus.COMPLETED), user))
                .orElse(List.of());
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

    public void sendTradeUpdates(User user) {
        tradeUpdateService.sendTradeUpdates(user);
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
        sendTradeUpdates(user);
        return order;
    }

    @Override
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
        if (stock == null) throw new InvalidTradeException("Invalid stock");

        if (trade.getSide() == Side.BUY) {
            tradeExecutionService.buyTrade((Order) trade, stock);
        } else {
            tradeExecutionService.sellTrade((Order) trade, stock);
        }

        userService.sendUserUpdate(trade.getUser());
        sendTradeUpdates(trade.getUser());
    }
}
