package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.decorator.*;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.Order;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
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
    private final WebSocketService webSocketService;

    public TradeExecutionService(TradeRepository tradeRepository, UserService userService, Notifier notifier,
                                 WebSocketService webSocketService) {
        this.tradeRepository = tradeRepository;
        this.userService = userService;
        this.notifier = notifier;
        this.webSocketService = webSocketService;
    }

    @Transactional
    public void buyTrade(Order order, Stock stock) {
        Trade trade = (Trade) order;
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        if (!userService.hasSufficientBalance(trade.getUser(), totalPrice)) {
            trade.setStatus(TradeStatus.FAILED);
            trade.setExecutedAt(LocalDateTime.now());
            tradeRepository.save(trade);
            Notifier notifier = buildNotifier(trade.getUser());
            notifier.notify(trade.getUser(), trade);
            return;
        }
        order.execute(stock.getCurrentPrice(), totalPrice);
        userService.deductFunds(trade.getUser(), totalPrice);
        userService.updateStockHoldingEntry(trade.getUser(), stock, trade);
        tradeRepository.save(trade);
        Notifier notifier = buildNotifier(trade.getUser());
        notifier.notify(trade.getUser(), trade);
    }

    @Transactional
    public void sellTrade(Order order, Stock stock) {
        Trade trade = (Trade) order;
        BigDecimal totalPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        if (!userService.userHasStockHoldingAndQuantity(trade.getUser(), stock, trade)) {
            trade.setStatus(TradeStatus.FAILED);
            trade.setExecutedAt(LocalDateTime.now());
            tradeRepository.save(trade);
            Notifier notifier = buildNotifier(trade.getUser());
            notifier.notify(trade.getUser(), trade);
            return;
        }
        order.execute(stock.getCurrentPrice(), totalPrice);
        userService.addFunds(trade.getUser(), totalPrice);
        userService.updateStockHoldingEntry(trade.getUser(), stock, trade);
        tradeRepository.save(trade);
        Notifier notifier = buildNotifier(trade.getUser());
        notifier.notify(trade.getUser(), trade);
    }

    private Notifier buildNotifier(User user) {
        Notifier notifier = new ConsoleNotifier(); // base, always on

        if (user.isEmailEnabled())     notifier = new EmailNotifier(notifier);
        if (user.isSmsEnabled())       notifier = new SmsNotifier(notifier);
        if (user.isDashboardEnabled()) notifier = new DashboardNotifier(notifier, webSocketService);

        return notifier;
    }
}