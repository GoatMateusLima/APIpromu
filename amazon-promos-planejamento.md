# 🛒 Amazon Promos Bot — Planejamento do Projeto

## Visão Geral

MVP de recomendação de promoções da Amazon com envio automático para Telegram e exposição via API para um site já existente.

O usuário cadastra promoções manualmente (com link de associado, preço, imagem e descrição), a IA (Groq) reescreve a descrição de forma atrativa, e o sistema envia para o Telegram e expõe os dados para o site. Cada promoção expira automaticamente após 24h.

---
## Stack

| Camada | Tecnologia |
|--------|------------|
| API | Java 21 + Spring Boot 3 |
| Banco | PostgreSQL |
| Infraestrutura | Docker + Docker Compose |
| IA | Groq API (reescrita de descrição) |
| Notificação | Telegram Bot API |
| Amazon (futuro) | Amazon Product Advertising API |

---

## Estrutura de Pastas

```
amazon-promos/
│
├── docker-compose.yml              # Orquestra os 2 containers (api + postgres)
├── .env                            # Variáveis de ambiente (chaves, tokens, senhas)
├── .env.example                    # Exemplo de .env sem os valores reais
│
└── api/
    ├── Dockerfile                  # Imagem da API Java
    ├── pom.xml                     # Dependências Maven
    │
    └── src/main/java/com/amazonpromos/
        │
        ├── AmazonPromosApplication.java    # Ponto de entrada do Spring Boot
        │
        ├── config/
        │   └── CorsConfig.java             # Permite o site consumir a API
        │
        ├── model/
        │   └── Promocao.java               # Entidade do banco (tabela promocao)
        │
        ├── repository/
        │   └── PromocaoRepository.java     # Acesso ao banco via JPA
        │
        ├── service/
        │   ├── GroqService.java            # Chama a API do Groq para reescrever descrição
        │   ├── TelegramService.java        # Envia mensagens para o grupo do Telegram
        │   └── PromocaoService.java        # Regras de negócio (salvar, buscar, expirar)
        │
        └── controller/
            ├── ManualController.java       # POST /manual — cadastro manual de promoção
            ├── EnviarController.java       # POST /enviar — envia tudo para o Telegram
            ├── EnviarSiteController.java   # GET /enviarsite — retorna promoções ativas para o site
            ├── AutomaticoController.java   # POST /automatico — inativo por enquanto (esqueleto)
            └── BancoController.java        # GET /banco — lista tudo no banco (uso interno/debug)
```

---

## Endpoints

### `POST /manual`
Recebe os dados da promoção preenchidos manualmente, chama o Groq para reescrever a descrição e salva no banco.

**Body (JSON):**
```json
{
  "titulo": "Fone JBL Tune 520BT",
  "preco": "R$ 199,90",
  "link": "https://amzn.to/XXXXXX ou https://www.amazon.com.br/dp/B08N5WRWNW",
  "imagem": "https://m.media-amazon.com/images/I/...",
  "descricaoOriginal": "Fone bluetooth com 57 horas de bateria e conexão multiponto.",
  "adicionarAfiliado": false // true para adicionar ID de afiliado ao link
}
```

**Fluxo interno:**
1. Recebe os dados
2. Se `adicionarAfiliado` for true e link não tiver tag, adiciona `?tag=seuID` ao link
3. Manda a descrição para o Groq → recebe descrição reescrita
4. Salva no banco com `expira_em = agora + 24h`
5. Retorna a promoção salva

---

### `POST /enviar`
Busca todas as promoções ativas no banco e envia cada uma como mensagem formatada no grupo do Telegram.

**Formato da mensagem:**
```
🔥 Fone JBL Tune 520BT

💰 R$ 199,90
📝 [Descrição gerada pelo Groq]

🛒 Comprar: https://amzn.to/XXXXXX
```

---

### `GET /enviarsite`
Retorna todas as promoções ativas (não expiradas) em JSON para o site consumir.

**Response (JSON):**
```json
[
  {
    "id": 1,
    "titulo": "Fone JBL Tune 520BT",
    "preco": "R$ 199,90",
    "link": "https://amzn.to/XXXXXX",
    "imagem": "https://m.media-amazon.com/images/I/...",
    "descricaoGerada": "...",
    "expiraEm": "2025-05-01T18:00:00"
  }
]
```

---

### `POST /automatico` *(inativo)*
Futuro endpoint que receberá uma palavra-chave ou categoria e buscará produtos automaticamente via Amazon Product Advertising API. Só será ativado após atingir 3 vendas qualificadas no programa de associados.

---

### `GET /banco`
Lista todas as promoções no banco, incluindo as já expiradas. Uso interno para debug.

---

## Banco de Dados — Tabela `promocao`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | SERIAL (PK) | ID auto-incremento |
| `titulo` | VARCHAR(255) | Nome do produto |
| `preco` | VARCHAR(50) | Preço formatado |
| `link` | TEXT | URL com link de associado |
| `imagem` | TEXT | URL da imagem do produto |
| `descricao_original` | TEXT | Descrição que o usuário digitou |
| `descricao_gerada` | TEXT | Descrição reescrita pelo Groq |
| `criado_em` | TIMESTAMP | Data/hora de cadastro |
| `expira_em` | TIMESTAMP | `criado_em + 24 horas` |

As consultas para o site e Telegram filtram por `expira_em > NOW()` automaticamente.

---

## Docker Compose — 2 Containers

```
docker-compose.yml
├── container: postgres
│   └── banco PostgreSQL com volume persistente
│
└── container: api
    └── Spring Boot rodando na porta 8080
        └── depende do container postgres estar saudável
```

---

## Variáveis de Ambiente (`.env`)

```env
# Banco
POSTGRES_DB=amazonpromos
POSTGRES_USER=postgres
POSTGRES_PASSWORD=suasenha

# Telegram
TELEGRAM_BOT_TOKEN=seu_token_aqui
TELEGRAM_CHAT_ID=seu_chat_id_aqui

# Groq
GROQ_API_KEY=sua_chave_aqui
```

> ⚠️ **Nunca commitar o `.env` no Git.** Adicionar ao `.gitignore`.

---

## Fluxo Completo (Manual)

```
Usuário preenche os dados
        ↓
POST /manual
        ↓
GroqService reescreve a descrição
        ↓
PromocaoService salva no banco com expira_em = +24h
        ↓
        ┌─────────────────────────────┐
        │                             │
POST /enviar                  GET /enviarsite
        │                             │
TelegramService envia        Retorna JSON para o site
mensagens no grupo
```

---

## Próximos Passos

1. Criar estrutura do projeto Spring Boot
2. Configurar `docker-compose.yml` com API + PostgreSQL
3. Criar entidade `Promocao` e repository
4. Implementar `GroqService`
5. Implementar `TelegramService`
6. Implementar controllers na ordem: Manual → EnviarSite → Enviar → Banco
7. Testar tudo localmente
8. Ativar `/automatico` após liberação da Amazon API
