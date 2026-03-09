package iu.devinmehringer.project1.mapper;

import iu.devinmehringer.project1.dto.stock.StockResponse;
import iu.devinmehringer.project1.model.stock.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper implements Mapper<Stock, StockResponse>{

    @Override
    public StockResponse toDTO(Stock entity) {
        StockResponse stockResponse = new StockResponse();
        stockResponse.setTicker(entity.getTicker());
        stockResponse.setPrice(entity.getCurrentPrice());
        return stockResponse;
    }
}
