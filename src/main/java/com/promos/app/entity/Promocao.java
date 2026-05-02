package com.promos.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promocao")
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(name = "preco", columnDefinition = "TEXT")
    private String preco;
    
    @Column(name = "precoPromo", columnDefinition = "TEXT")
    private String precoPromo;

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(columnDefinition = "TEXT")
    private String imagem;

    @Column(name = "descricao_original", columnDefinition = "TEXT")
    private String descricaoOriginal;

    @Column(name = "descricao_gerada", columnDefinition = "TEXT")
    private String descricaoGerada;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getPrecoPromo() {
        return precoPromo;
    }

    public void setPrecoPromo(String precoPromo) {
        this.precoPromo = precoPromo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getDescricaoOriginal() {
        return descricaoOriginal;
    }

    public void setDescricaoOriginal(String descricaoOriginal) {
        this.descricaoOriginal = descricaoOriginal;
    }

    public String getDescricaoGerada() {
        return descricaoGerada;
    }

    public void setDescricaoGerada(String descricaoGerada) {
        this.descricaoGerada = descricaoGerada;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(LocalDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }
}