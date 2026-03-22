package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.repository.StockRepository;
import iu.devinmehringer.project1.strategy.PriceStrategy;
import iu.devinmehringer.project1.strategy.Tradeable;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class StockService implements Tradeable<Stock>, CommandLineRunner {

    private final StockRepository stockRepository;
    private PriceStrategy priceStrategy;
    private final WebSocketService webSocketService;
    private final Map<Long, Stock> stockCache = new HashMap<>();

    public StockService(StockRepository stockRepository, WebSocketService webSocketService) {
        this.stockRepository = stockRepository;
        this.webSocketService = webSocketService;
    }

    @PostConstruct
    public void init() {
        stockRepository.findAll().forEach(s -> stockCache.put(s.getId(), s));
    }

    @Override
    public void updateAll() {
        stockCache.values().forEach(this::updateOne);
        webSocketService.sendStocks(new ArrayList<>(stockCache.values()));
    }

    @Override
    public void updateOne(Stock stock) {
        BigDecimal newPrice = priceStrategy.nextStep(stock);
        stock.setCurrentPrice(newPrice);
        stockRepository.save(stock);
    }

    @Override
    public void setPriceStrategy(PriceStrategy priceStrategy) {
        this.priceStrategy = priceStrategy;
    }

    @Override
    public String getDefaultStrategy() {
        return "RandomWalk";
    }

    @Override
    public void run(String... args) throws Exception {
        if (stockRepository.count() == 0) {
            stockRepository.saveAll(defaultStocks());
        }
        stockRepository.findAll().forEach(s -> stockCache.put(s.getId(), s));
    }

    private List<Stock> defaultStocks() {
        return List.of(
                new Stock("AAPL", BigDecimal.valueOf(257.00)),
                new Stock("MSFT", BigDecimal.valueOf(409.00)),
                new Stock("GOOGL", BigDecimal.valueOf(398.00)),
                new Stock("NVDA", BigDecimal.valueOf(178.00)),
                new Stock("META", BigDecimal.valueOf(645.00)),
                new Stock("JPM", BigDecimal.valueOf(289.00)),
                new Stock("BAC", BigDecimal.valueOf(49.00)),
                new Stock("AMZN", BigDecimal.valueOf(213.00)),
                new Stock("TSLA", BigDecimal.valueOf(397.00)),
                new Stock("JNJ", BigDecimal.valueOf(240.00))
        );
    }

    public List<Stock> getAllStocks() {
        return this.stockRepository.findAll();
    }

    public boolean StockTickerExists(String ticker) {
        return stockRepository.findByTicker(ticker).isPresent();
    }

    public Stock getStockByTicker(String ticker) {
        Optional<Stock> stock = stockRepository.findByTicker(ticker);
        return stock.orElse(null);
    }
}
