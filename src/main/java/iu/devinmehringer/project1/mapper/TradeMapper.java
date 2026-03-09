package iu.devinmehringer.project1.mapper;

import iu.devinmehringer.project1.dto.trade.TradeResponse;
import iu.devinmehringer.project1.model.trade.LimitOrder;
import iu.devinmehringer.project1.model.trade.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper implements Mapper<Trade, TradeResponse>{
    @Override
    public TradeResponse toDTO(Trade entity) {
        TradeResponse tradeResponse = new TradeResponse();
        tradeResponse.setUserID(entity.getUser().getId());
        tradeResponse.setOrderType(entity.getOrderType());
        tradeResponse.setTicker(entity.getTicker());
        tradeResponse.setQuantity(entity.getQuantity());
        tradeResponse.setSide(entity.getSide());
        tradeResponse.setPrice(entity.getPrice());
        tradeResponse.setTotalPrice(entity.getTotalPrice());
        tradeResponse.setCreatedAt(entity.getCreatedAt());
        tradeResponse.setExecutedAt(entity.getExecutedAt());
        tradeResponse.setStatus(entity.getStatus());
        if (entity instanceof LimitOrder) {
            tradeResponse.setLimitPrice(((LimitOrder) entity).getLimitPrice());
            tradeResponse.setType(((LimitOrder) entity).getType());
        }
        return tradeResponse;
    }
}
