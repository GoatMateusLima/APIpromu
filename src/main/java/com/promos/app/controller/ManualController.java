package com.promos.app.controller;

import com.promos.app.dto.PromocaoRequest;
import com.promos.app.entity.Promocao;
import com.promos.app.service.PromocaoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manual")
public class ManualController {

    private final PromocaoService service;

    public ManualController(PromocaoService service) {
        this.service = service;
    }

    @PostMapping
    public Promocao cadastrar(@RequestBody PromocaoRequest request) {
        return service.salvarPromocao(request);
    }
}