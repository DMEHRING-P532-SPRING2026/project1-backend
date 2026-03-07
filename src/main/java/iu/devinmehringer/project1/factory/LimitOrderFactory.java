package iu.devinmehringer.project1.factory;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.model.trade.LimitOrder;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import org.springframework.stereotype.Component;

@Component("LIMIT")
public class LimitOrderFactory extends OrderFactory {
    @Override
    public Trade createOrder(TradeRequest tradeRequest) {
        return new LimitOrder(tradeRequest.getTicker(),
                tradeRequest.getQuantity(), tradeRequest.getSide(),
                TradeStatus.PENDING, tradeRequest.getLimitPrice(),
                tradeRequest.getConditionType());
    }
}
