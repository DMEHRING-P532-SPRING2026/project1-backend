package iu.devinmehringer.project1.service;

import iu.devinmehringer.project1.dto.stock.StockResponse;
import iu.devinmehringer.project1.dto.trade.TradeResponse;
import iu.devinmehringer.project1.dto.user.UserResponse;
import iu.devinmehringer.project1.mapper.StockMapper;
import iu.devinmehringer.project1.mapper.TradeMapper;
import iu.devinmehringer.project1.mapper.UserMapper;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final UserMapper userMapper;
    private final TradeMapper tradeMapper;
    private final StockMapper stockMapper;

    public WebSocketService(UserMapper userMapper, TradeMapper tradeMapper, StockMapper stockMapper) {
        this.userMapper = userMapper;
        this.tradeMapper = tradeMapper;
        this.stockMapper = stockMapper;
    }

    public void sendUserUpdate(User user) {
        UserResponse ur = userMapper.toDTO(user);
        messagingTemplate.convertAndSend("/topic/user/" + ur.getUserId(), ur);
    }

    public void sendPendingTradeUpdate(List<Trade> trades, User user) {
        List<TradeResponse> tr = trades.stream().map(tradeMapper::toDTO).toList();
        messagingTemplate.convertAndSend("/topic/trade/pending/" + user.getId(), tr);
    }

    public void sendExecutedTradeUpdate(List<Trade> trades, User user) {
        List<TradeResponse> tr = trades.stream().map(tradeMapper::toDTO).toList();
        messagingTemplate.convertAndSend("/topic/trade/executed/" + user.getId(), tr);
    }

    public void sendStocks(List<Stock> stocks) {
        messagingTemplate.convertAndSend("/topic/stocks", stocks.stream().map(stockMapper::toDTO).toList());
    }
}