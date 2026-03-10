package iu.devinmehringer.project1.controller;

import iu.devinmehringer.project1.dto.stock.StockResponse;
import iu.devinmehringer.project1.mapper.StockMapper;
import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.observer.Observer;
import iu.devinmehringer.project1.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController implements Observer {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final StockService stockService;
    private final StockMapper stockMapper;

    public StockController(StockService stockService, StockMapper stockMapper) {
        this.stockService = stockService;
        this.stockMapper = stockMapper;
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStocks().stream().map(stockMapper::toDTO).toList());
    }

    public void updateStock() {
        List<StockResponse> stocks = stockService.getAllStocks()
                .stream()
                .map(stockMapper::toDTO)
                .toList();
        messagingTemplate.convertAndSend("/topic/stocks", stocks);
    }

    @Override
    public void update() {
        updateStock();
    }
}
