package iu.devinmehringer.project1.controller;

import iu.devinmehringer.project1.dto.trade.TradeRequest;
import iu.devinmehringer.project1.dto.trade.TradeResponse;
import iu.devinmehringer.project1.mapper.TradeMapper;
import iu.devinmehringer.project1.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;
    private final TradeMapper tradeMapper;

    public TradeController(TradeService tradeService, TradeMapper tradeMapper) {
        this.tradeService = tradeService;
        this.tradeMapper = tradeMapper;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TradeResponse>> getAllPending() {
        return ResponseEntity.ok(tradeService.getAllPending().stream().map(tradeMapper::toDTO).toList());
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TradeResponse>> getAllExecuted() {
        return ResponseEntity.ok(tradeService.getAllExecuted().stream().map(tradeMapper::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<TradeResponse> createTrade(@RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradeMapper.toDTO(tradeService.createTrade(request)));
    }
}
