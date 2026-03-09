package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.repository.StockRepository;
import iu.devinmehringer.project1.strategy.PriceStrategy;
import iu.devinmehringer.project1.strategy.Tradeable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StockService implements Tradeable<Stock>, CommandLineRunner {

    private final StockRepository stockRepository;
    private PriceStrategy priceStrategy;

    public StockService(StockRepository stockRepository, PriceStrategy priceStrategy) {
        this.stockRepository = stockRepository;
        this.priceStrategy = priceStrategy;
    }

    @Override
    public void updateAll() {
        stockRepository.findAll().forEach(this::updateOne);
    }

    @Override
    public void updateOne(Stock stock) {
        BigDecimal newPrice = priceStrategy.nextStep(stock.getCurrentPrice());
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
