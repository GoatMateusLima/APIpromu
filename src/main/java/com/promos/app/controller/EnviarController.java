package com.promos.app.controller;

import com.promos.app.entity.Promocao;
import com.promos.app.service.PromocaoService;
import com.promos.app.service.TelegramService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnviarController {

    private final PromocaoService promocaoService;
    private final TelegramService telegramService;

    public EnviarController(PromocaoService promocaoService, TelegramService telegramService) {
        this.promocaoService = promocaoService;
        this.telegramService = telegramService;
    }

    // Envia apenas o último produto cadastrado para o Telegram
    @PostMapping("/enviar")
    public String enviarUltimo() {
        List<Promocao> ativas = promocaoService.buscarAtivas();

        if (ativas.isEmpty()) {
            System.out.println("⚠️  Nenhuma promoção ativa no banco.");
            return "Nenhuma promoção ativa no banco.";
        }

        Promocao ultimo = ativas.stream()
                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                .get();

        System.out.println("\n═══════════════════════════════════");
        System.out.println("📦 Enviando último produto ao Telegram");
        System.out.println("🆔 ID:     " + ultimo.getId());
        System.out.println("🎯 Título: " + ultimo.getTitulo());
        System.out.println("💰 Preço:  " + ultimo.getPreco());
        System.out.println("🖼️  Imagem: " + ultimo.getImagem());
        System.out.println("🔗 Link:   " + ultimo.getLink());
        System.out.println("═══════════════════════════════════\n");

        telegramService.enviarFotoComDescricao(
                ultimo.getImagem(),
                ultimo.getTitulo(),
                ultimo.getPreco(),
                ultimo.getDescricaoGerada(),
                ultimo.getLink()
        );

        return "✅ Enviado: " + ultimo.getTitulo();
    }

    // Envia todos os produtos ativos para o Telegram (uso manual/emergência)
    @PostMapping("/enviar/todos")
    public String enviarTodos() {
        List<Promocao> ativas = promocaoService.buscarAtivas();

        if (ativas.isEmpty()) {
            System.out.println("⚠️  Nenhuma promoção ativa no banco.");
            return "Nenhuma promoção ativa no banco.";
        }

        System.out.println("\n═══════════════════════════════════");
        System.out.println("📢 Enviando " + ativas.size() + " promoção(ões) para o Telegram");
        System.out.println("═══════════════════════════════════\n");

        for (Promocao p : ativas) {
            System.out.println("📦 Enviando ID " + p.getId() + ": " + p.getTitulo());
            telegramService.enviarFotoComDescricao(
                    p.getImagem(),
                    p.getTitulo(),
                    p.getPreco(),
                    p.getDescricaoGerada(),
                    p.getLink()
            );
        }

        return "✅ Enviadas " + ativas.size() + " promoções para o Telegram.";
    }
}