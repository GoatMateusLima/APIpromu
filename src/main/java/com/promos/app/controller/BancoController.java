package com.promos.app.controller;

import com.promos.app.entity.Promocao;
import com.promos.app.service.PromocaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BancoController {

    private final PromocaoService service;

    public BancoController(PromocaoService service) {
        this.service = service;
    }

    @GetMapping("/banco")
    public List<Promocao> listarTodas() {
        return service.buscarTodas();
    }

    @DeleteMapping("/banco/{id}")
    public void deletarPorId(@PathVariable Long id) {
        service.deletarPorId(id);
    }
}