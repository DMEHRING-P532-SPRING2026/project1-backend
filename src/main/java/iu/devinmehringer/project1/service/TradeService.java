package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.factory.OrderFactory;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final Map<String, OrderFactory> factories;

    @Autowired
    public TradeService(TradeRepository tradeRepository, Map<String, OrderFactory> factories) {
        this.tradeRepository = tradeRepository;
        this.factories = factories;
    }

    public List<Trade> getAllPending() {
        return tradeRepository.findByStatus(TradeStatus.PENDING);
    }

    public Trade createTrade(TradeRequest request) {
        OrderFactory factory = OrderFactory.getFactory(request.getOrderType().name(), factories);
        Trade trade = factory.createOrder(request);
        return tradeRepository.save(trade);
    }
}
