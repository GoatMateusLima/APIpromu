package com.promos.app.scheduler;

import com.promos.app.repository.PromocaoRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class PromocaoScheduler {

    private final PromocaoRepository repository;

    public PromocaoScheduler(PromocaoRepository repository) {
        this.repository = repository;
    }

    /**
     * Deleta automaticamente todas as promoções expiradas a cada 1 hora
     */
    @Scheduled(fixedRate = 3600000) // 1 hora em milissegundos
    @Transactional 
    public void deletarExpiradas() {
        LocalDateTime agora = LocalDateTime.now();
        long deletadas = repository.deleteByExpiraEmBefore(agora);
        if (deletadas > 0) {
            System.out.println("✓ " + deletadas + " promoção(ões) expirada(s) deletada(s) automaticamente.");
        }
    }
}
