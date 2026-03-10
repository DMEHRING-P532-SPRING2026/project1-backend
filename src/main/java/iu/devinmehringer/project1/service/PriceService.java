package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.controller.StockController;
import iu.devinmehringer.project1.exception.UnknownStrategyTypeException;
import iu.devinmehringer.project1.observer.Observer;
import iu.devinmehringer.project1.observer.Subject;
import iu.devinmehringer.project1.strategy.PriceStrategy;
import iu.devinmehringer.project1.strategy.Tradeable;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PriceService implements Subject {
    private final List<Tradeable<?>> tradeables;
    private final Map<String, PriceStrategy> strategies;
    private final List<Observer> observers = new ArrayList<>();

    public PriceService(List<Tradeable<?>> tradeables, Map<String, PriceStrategy> strategies,
                        TradeService tradeService) {
        this.tradeables = tradeables;
        this.strategies = strategies;
        this.addObserver(tradeService);
    }

    @PostConstruct
    public void assignDefaultStrategies() {
        for (Tradeable<?> tradeable : tradeables) {
            PriceStrategy priceStrategy = strategies.get(tradeable.getDefaultStrategy());
            if (priceStrategy == null) {
                throw new UnknownStrategyTypeException(tradeable.getDefaultStrategy());
            }
            tradeable.setPriceStrategy(priceStrategy);
        }
    }

    @Scheduled(fixedRateString = "${price.update.rate}")
    public void priceUpdate() {
        tradeables.forEach(Tradeable::updateAll);
        notifyObservers();
    }


    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }
}
