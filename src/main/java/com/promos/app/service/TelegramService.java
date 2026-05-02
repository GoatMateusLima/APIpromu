package com.promos.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarMensagem(String mensagem) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, String> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", mensagem);
        body.put("parse_mode", "HTML");

        restTemplate.postForEntity(url, body, String.class);
    }

    public void testarConexao() {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            Map<String, String> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", "🧪 Teste de conexão - API funcionando!");
            body.put("parse_mode", "HTML");

            var result = restTemplate.postForEntity(url, body, String.class);
            System.out.println("✅ Teste enviado! Status: " + result.getStatusCode());

        } catch (Exception e) {
            System.err.println("❌ Erro no teste de conexão: " + e.getMessage());
        }
    }

    public void enviarFotoComDescricao(String urlImagem, String titulo, String preco, String precoPromo, String descricao, String link) {
        try {
            String caption = "🔥 <b>" + titulo + "</b>\n\n" +
                             "💰 <s>" + preco + "</s>\n" +
                             "💥 <b>" + precoPromo + "</b>\n" +
                             "📝 " + descricao;

            // Botão inline de compra
            Map<String, String> botao = new HashMap<>();
            botao.put("text", "🛒 Comprar agora");
            botao.put("url", link);

            Map<String, Object> replyMarkup = new HashMap<>();
            replyMarkup.put("inline_keyboard", List.of(List.of(botao)));

            String url = "https://api.telegram.org/bot" + botToken + "/sendPhoto";

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("photo", urlImagem);
            body.put("caption", caption);
            body.put("parse_mode", "HTML");
            body.put("reply_markup", replyMarkup);

            var result = restTemplate.postForEntity(url, body, String.class);
            System.out.println("✅ Foto enviada! Status: " + result.getStatusCode());

        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar foto ao Telegram: " + e.getMessage());

            // Fallback: manda como texto simples com botão
            try {
                String texto = "🔥 <b>" + titulo + "</b>\n\n" +
                               "💰 <s>" + preco + "</s>\n" +
                               "💥 <b>" + precoPromo + "</b>\n" +
                               "📝 " + descricao;

                Map<String, String> botao = new HashMap<>();
                botao.put("text", "🛒 Comprar agora");
                botao.put("url", link);

                Map<String, Object> replyMarkup = new HashMap<>();
                replyMarkup.put("inline_keyboard", List.of(List.of(botao)));

                String urlMsg = "https://api.telegram.org/bot" + botToken + "/sendMessage";

                Map<String, Object> bodyMsg = new HashMap<>();
                bodyMsg.put("chat_id", chatId);
                bodyMsg.put("text", texto);
                bodyMsg.put("parse_mode", "HTML");
                bodyMsg.put("reply_markup", replyMarkup);

                restTemplate.postForEntity(urlMsg, bodyMsg, String.class);
                System.out.println("✅ Enviado como texto (fallback)");

            } catch (Exception ex) {
                System.err.println("❌ Fallback também falhou: " + ex.getMessage());
            }
        }
    }
}