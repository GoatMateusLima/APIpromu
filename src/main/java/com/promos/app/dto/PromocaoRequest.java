package com.promos.app.dto;

public class PromocaoRequest {
    private String titulo;
    private String preco;
    private String precoPromo;
    private String link; // pode ser link original ou já com afiliado
    private String imagem;
    private String descricaoOriginal;
    private boolean adicionarAfiliado; // se true, adiciona ID de afiliado ao link

    public String getPrecoPromo(){
        return precoPromo;
    }

    public void setPrecoPromo(String precoPromo){
        this.precoPromo = precoPromo;
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

    public boolean isAdicionarAfiliado() {
        return adicionarAfiliado;
    }

    public void setAdicionarAfiliado(boolean adicionarAfiliado) {
        this.adicionarAfiliado = adicionarAfiliado;
    }
}