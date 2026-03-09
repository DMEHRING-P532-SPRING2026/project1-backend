package iu.devinmehringer.project1.mapper;

import iu.devinmehringer.project1.dto.StockHolding.StockHoldingResponse;
import iu.devinmehringer.project1.model.StockHolding.StockHolding;
import org.springframework.stereotype.Component;

@Component
public class StockHoldingMapper implements Mapper<StockHolding, StockHoldingResponse> {
    @Override
    public StockHoldingResponse toDTO(StockHolding entity) {
        StockHoldingResponse stockHoldingResponse = new StockHoldingResponse();
        stockHoldingResponse.setTicker(entity.getStock().getTicker());
        stockHoldingResponse.setQuantity(entity.getQuantity());
        return stockHoldingResponse;
    }
}
