package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.controller.UserController;
import iu.devinmehringer.project1.exception.InvalidTradeException;
import iu.devinmehringer.project1.model.StockHolding.StockHolding;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.Side;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.StockHoldingRepository;
import iu.devinmehringer.project1.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    @Value("${user.default.balance}")
    private BigDecimal defaultBalance;
    private final UserRepository userRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final WebSocketService webSocketService;

    public UserService(UserRepository userRepository, StockHoldingRepository stockHoldingRepository,
                       WebSocketService webSocketService) {
        this.userRepository = userRepository;
        this.stockHoldingRepository = stockHoldingRepository;
        this.webSocketService = webSocketService;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User user = new User(defaultBalance);
            userRepository.save(user);
        }
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public boolean hasSufficientBalance(User user, BigDecimal amount) {
        return user.hasSufficientBalance(amount);
    }

    public void deductFunds(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().subtract(amount));
        this.userRepository.save(user);
    }

    public void addFunds(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        this.userRepository.save(user);
    }

    public StockHolding createNewStockHoldingEntry(User user, Stock stock, Integer quantity) {
        StockHolding stockHolding = new StockHolding(user, stock, quantity);
        return stockHoldingRepository.save(stockHolding);
    }

    public StockHolding getStockHoldingByUserAndStock(User user, Stock stock) {
        Optional<StockHolding> stockHolding = this.stockHoldingRepository.findByUserAndStock(user, stock);
        return stockHolding.orElse(null);
    }

    public void updateStockHoldingEntry(User user, Stock stock, Trade trade) {
        StockHolding stockHolding = getStockHoldingByUserAndStock(user, stock);
        if (trade.getSide() == Side.BUY) {
            if (stockHolding != null) {
                stockHolding.setQuantity(trade.getQuantity() + stockHolding.getQuantity());
            } else {
                stockHolding = createNewStockHoldingEntry(user, stock, trade.getQuantity());
            }
            stockHoldingRepository.save(stockHolding);
        } else {
            if (stockHolding == null) throw new InvalidTradeException("No holding found for " + stock.getTicker());
            stockHolding.setQuantity(stockHolding.getQuantity() - trade.getQuantity());
            if (stockHolding.getQuantity() <= 0) {
                stockHoldingRepository.delete(stockHolding);
            } else {
                stockHoldingRepository.save(stockHolding);
            }
        }
    }

    public boolean userHasStockHoldingAndQuantity(User user, Stock stock, Trade trade) {
        StockHolding stockHolding = getStockHoldingByUserAndStock(user, stock);
        if (stockHolding != null) {
            return stockHolding.getQuantity() >= trade.getQuantity();
        }
        return false;
    }

    public void sendUserUpdate(User user) {
        this.webSocketService.sendUserUpdate(user);
    }

}