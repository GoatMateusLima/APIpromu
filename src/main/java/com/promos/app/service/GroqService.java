package com.promos.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String reescreverDescricao(String descricaoOriginal) {
        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            String prompt = "Você é um especialista em marketing. Reescreva APENAS um texto super atrativo e persuasivo com no máximo 150 caracteres para vender este produto. Seja direto, use emojis se necessário e crie urgência. Descrição original: " + descricaoOriginal;

            Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", new Object[]{
                    Map.of("role", "user", "content", prompt)
                },
                "max_tokens", 300,
                "temperature", 0.8
            );

            System.out.println("📡 Enviando para Groq com chave: " + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "INVÁLIDA"));
            System.out.println("📝 Prompt: " + prompt);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            System.out.println("✓ Status Groq: " + response.getStatusCode());
            System.out.println("📦 Response body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                var body = response.getBody();
                System.out.println("📋 Body keys: " + body.keySet());

                if (body.containsKey("choices")) {
                    var choices = (java.util.List) body.get("choices");
                    System.out.println("🎯 Choices encontradas: " + (choices != null ? choices.size() : 0));

                    if (choices != null && !choices.isEmpty()) {
                        var choice = (Map) choices.get(0);
                        System.out.println("📄 Choice keys: " + choice.keySet());

                        if (choice.containsKey("message")) {
                            var message = (Map) choice.get("message");
                            if (message.containsKey("content")) {
                                String content = (String) message.get("content");
                                System.out.println("✅ Groq gerou com sucesso: " + content);
                                return content.trim();
                            }
                        }
                    }
                } else if (body.containsKey("error")) {
                    System.err.println("❌ Erro da API Groq: " + body.get("error"));
                }
            }

            System.err.println("⚠️ Groq retornou algo inesperado, usando fallback");
            return descricaoOriginal;

        } catch (Exception e) {
            System.err.println("❌ Erro ao chamar Groq API: " + e.getMessage());
            e.printStackTrace();
            return descricaoOriginal;
        }
    }
}