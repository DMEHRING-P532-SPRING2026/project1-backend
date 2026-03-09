package iu.devinmehringer.project1.strategy;

public interface Tradeable<T> {
    void updateAll();
    void updateOne(T item);
    void setPriceStrategy(PriceStrategy priceStrategy);
    String getDefaultStrategy();
}
