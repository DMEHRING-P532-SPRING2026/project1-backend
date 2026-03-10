package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
public class TradeUpdateService {

    private final TradeRepository tradeRepository;
    private final WebSocketService webSocketService;

    public TradeUpdateService(TradeRepository tradeRepository, WebSocketService webSocketService) {
        this.tradeRepository = tradeRepository;
        this.webSocketService = webSocketService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendTradeUpdates(User user) {
        List<Trade> pending = tradeRepository.findByStatusAndUser(TradeStatus.PENDING, user);
        List<Trade> executed = tradeRepository.findByStatusInAndUser(
                List.of(TradeStatus.FAILED, TradeStatus.COMPLETED), user);
        webSocketService.sendPendingTradeUpdate(pending, user);
        webSocketService.sendExecutedTradeUpdate(executed, user);
    }
}
