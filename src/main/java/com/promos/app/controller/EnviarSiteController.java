package com.promos.app.controller;

import com.promos.app.entity.Promocao;
import com.promos.app.service.PromocaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnviarSiteController {

    private final PromocaoService service;

    public EnviarSiteController(PromocaoService service) {
        this.service = service;
    }

    @GetMapping("/enviarsite")
    public List<Promocao> listarAtivas() {
        return service.buscarAtivas();
    }
}