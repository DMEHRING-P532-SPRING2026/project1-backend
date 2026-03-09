package iu.devinmehringer.project1.factory;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.model.trade.MarketOrder;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.stereotype.Component;

@Component("MARKET")
public class MarketOrderFactory extends OrderFactory {
    @Override
    public Trade createOrder(User user, TradeRequest tradeRequest) {
        return new MarketOrder(user, tradeRequest.getTicker(), tradeRequest.getQuantity(),
                tradeRequest.getSide(), TradeStatus.PENDING);
    }
}
