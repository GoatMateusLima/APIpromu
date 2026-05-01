package com.promos.app.controller;

import com.promos.app.service.TelegramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    private final TelegramService telegramService;

    public TesteController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @GetMapping("/teste/telegram")
    public String testarTelegram() {
        telegramService.testarConexao();
        return "Teste enviado para Telegram - verifique os logs!";
    }
}