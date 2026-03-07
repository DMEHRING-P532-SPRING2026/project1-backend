package iu.devinmehringer.project1.controller;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getAllPending() {
        return ResponseEntity.ok(tradeService.getAllPending());
    }

    @PostMapping
    public ResponseEntity<Trade> createTrade(@RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradeService.createTrade(request));
    }
}
