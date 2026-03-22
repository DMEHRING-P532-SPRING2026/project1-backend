package iu.devinmehringer.project1.controller;

import iu.devinmehringer.project1.dto.stock.StockResponse;
import iu.devinmehringer.project1.dto.stock.StrategyRequest;
import iu.devinmehringer.project1.mapper.StockMapper;
import iu.devinmehringer.project1.service.PriceService;
import iu.devinmehringer.project1.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    @Autowired
    private final StockService stockService;
    private final StockMapper stockMapper;
    private final PriceService priceService;

    public StockController(StockService stockService, StockMapper stockMapper, PriceService priceService) {
        this.stockService = stockService;
        this.priceService = priceService;
        this.stockMapper = stockMapper;
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStocks().stream().map(stockMapper::toDTO).toList());
    }

    @PostMapping("/strategy")
    public ResponseEntity<Void> setStrategy(@RequestBody StrategyRequest strategyRequest) {
        priceService.setStrategyStock(strategyRequest.getStrategy());
        return ResponseEntity.ok().build();
    }

}
