package iu.devinmehringer.project1.factory;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.exception.UnknownOrderTypeException;
import iu.devinmehringer.project1.model.trade.Trade;

import java.util.Map;

public abstract class OrderFactory {
    public abstract Trade createOrder(TradeRequest request);

    public static OrderFactory getFactory(String orderType, Map<String, OrderFactory> factories) {
        OrderFactory factory = factories.get(orderType);
        if (factory == null) {
            throw new UnknownOrderTypeException("Unknown order type: " + orderType);
        }
        return factory;
    }
}
