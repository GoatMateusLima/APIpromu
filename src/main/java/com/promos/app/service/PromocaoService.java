package com.promos.app.service;

import com.promos.app.dto.PromocaoRequest;
import com.promos.app.entity.Promocao;
import com.promos.app.repository.PromocaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromocaoService {

    private final PromocaoRepository repository;
    private final GroqService groqService;

    private static final String AFILIADO_ID = "promubr-20"; // ID de afiliado da Amazon

    public PromocaoService(PromocaoRepository repository, GroqService groqService) {
        this.repository = repository;
        this.groqService = groqService;
    }

    public Promocao salvarPromocao(PromocaoRequest request) {
        Promocao promocao = new Promocao();
        promocao.setTitulo(request.getTitulo());
        promocao.setPreco(request.getPreco());
        promocao.setImagem(request.getImagem());
        promocao.setDescricaoOriginal(request.getDescricaoOriginal());

        // Lidar com o link
        String link = request.getLink();
        if (request.isAdicionarAfiliado() && !link.contains("amzn.to") && !link.contains("tag=")) {
            // Adicionar ID de afiliado se não estiver presente
            if (link.contains("?")) {
                link += "&tag=" + AFILIADO_ID;
            } else {
                link += "?tag=" + AFILIADO_ID;
            }
        }
        promocao.setLink(link);

        promocao.setCriadoEm(LocalDateTime.now());
        promocao.setExpiraEm(LocalDateTime.now().plusHours(24));
        promocao.setDescricaoGerada(groqService.reescreverDescricao(request.getDescricaoOriginal()));
        return repository.save(promocao);
    }

    public List<Promocao> buscarAtivas() {
        return repository.findAtivas();
    }

    public List<Promocao> buscarTodas() {
        return repository.findAll();
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }
}