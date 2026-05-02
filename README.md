# Amazon Promos API - Documentação Completa

## 📊 Status Atual do Projeto

### ✅ Implementado e Funcionando
- **Java 21 + Spring Boot 3.3.0** - Projeto totalmente compilado
- **PostgreSQL 16** - Banco de dados persistente com Docker
- **Lombok Removido** - Todas as classes usam getters/setters manuais (build estável)
- **Groq AI Integration** - Reescrita automática de descrições com Llama 3 (8B)
- **Telegram Bot API** - Envio de fotos + caption formatado em HTML
- **Amazon Associates** - Sistema de afiliados com ID `promubr-20`
- **Scheduler Automático** - Delete de promoções expiradas a cada hora
- **Porta Configurável** - Docker em 8085, local em 8080
- **Nova Coluna precoPromo** - Adicionado campo para valor promocional do produto

### 🔧 Tecnologias
| Componente | Versão |
|-----------|--------|
| Java | 21 |
| Spring Boot | 3.3.0 |
| Spring Data JPA | Hibernate |
| PostgreSQL | 16 |
| Maven | 3.9.6 |
| Docker | Latest |

## 🚀 Quick Start

### Com Docker (Recomendado)
```bash
# Build e start
docker compose up --build

# A API estará em: http://localhost:8085
```

### Localmente sem Docker
```bash
# Compile
mvn clean package -DskipTests

# Execute
java -jar target/amazon-promos-1.0.0.jar

# A API estará em: http://localhost:8080
```

## 🔑 Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Banco de Dados
POSTGRES_PASSWORD=sua_senha_segura

# Telegram Bot
TELEGRAM_BOT_TOKEN=seu_token_aqui
TELEGRAM_CHAT_ID=seu_chat_id_aqui

# Groq AI
GROQ_API_KEY=sua_chave_groq_aqui
```

## 📋 Endpoints da API

### 1️⃣ POST `/manual` - Cadastrar Promoção
Cadastra um novo produto com reescrita automática via Groq.

**Request:**
```bash
curl -X POST http://localhost:8085/manual \
  -H 'Content-Type: application/json' \
  -d '{
    "titulo": "Apple iPhone 16 (256GB) Preto",
    "preco": "R$ 3.999,00",
    "precoPromo": "R$ 2.999,00",
    "link": "https://www.amazon.com.br/dp/B08N5WRWNW",
    "imagem": "https://m.media-amazon.com/images/I/...",
    "descricaoOriginal": "iPhone 16 com chip A18, câmera 48MP, bateria 22 horas...",
    "adicionarAfiliado": true
  }'
```

**Response:**
```json
{
  "id": 1,
  "titulo": "Apple iPhone 16 (256GB) Preto",
  "preco": "R$ 3.999,00",
  "precoPromo": "R$ 2.999,00",
  "link": "https://www.amazon.com.br/dp/B08N5WRWNW?tag=promubr-20",
  "imagem": "https://m.media-amazon.com/images/I/...",
  "descricaoOriginal": "...",
  "descricaoGerada": "🔥 iPhone 16 IMPERDÍVEL! Câmera 48MP + A18 + Bateria 22h. Compre AGORA! ⚡",
  "criadoEm": "2026-05-01T12:34:56",
  "expiraEm": "2026-05-02T12:34:56"
}
```

### 2️⃣ POST `/enviar` - Enviar para Telegram
Envia todas as promoções ativas para o Telegram com foto e formatação.

**Request:**
```bash
curl -X POST http://localhost:8085/enviar
```

**Resultado no Telegram:**
```
🔥 Apple iPhone 16 (256GB) Preto

💰 R$ 3.999,00
📝 🔥 iPhone 16 IMPERDÍVEL! Câmera 48MP + A18 + Bateria 22h. Compre AGORA! ⚡

🛒 Comprar aqui [link clicável]
```
(+ Foto do produto em anexo)

### 3️⃣ GET `/banco` - Listar Todas as Promoções
Retorna **todas** as promoções (ativas e expiradas).

```bash
curl http://localhost:8085/banco
```

### 4️⃣ DELETE `/banco/{id}` - Deletar Promoção Manualmente
Remove uma promoção específica do banco.

```bash
curl -X DELETE http://localhost:8085/banco/1
```

### 5️⃣ GET `/enviarsite` - Promoções Ativas em JSON
Retorna apenas promoções ativas para exibição em site/app.

```bash
curl http://localhost:8085/enviarsite
```

## 🤖 Como Funciona o Groq

### Processo Automático
1. Usuário envia promoção via `/manual`
2. Sistema chama Groq API com prompt específico
3. Groq reescreve em **≤150 caracteres** com:
   - 🔥 Emojis estratégicos
   - ⚡ Linguagem urgente
   - 📝 Mantém specs técnicas
4. Texto atrativo salvo no banco
5. Enviado ao Telegram com foto

### Prompt do Groq
```
"Você é um especialista em marketing. Reescreva APENAS um texto 
super atrativo e persuasivo com no máximo 150 caracteres para 
vender este produto. Seja direto, use emojis se necessário e 
crie urgência."
```

### Model Usado
- **llama3-8b-8192** (Groq)
- 300 tokens de limite
- Temperature: 0.8 (criativo)

## 🔄 Sistema de Expiração Automática

### Como Funciona
- Cada promoção é criada com `expiraEm = agora + 24 horas`
- Um **Scheduler** roda a cada 1 hora
- Verifica e deleta todas as expiradas automaticamente
- Log: `✓ X promoção(ões) expirada(s) deletada(s)`

### Componentes
- **Class**: `com/promos/app/scheduler/PromocaoScheduler.java`
- **Frequência**: 3.600.000ms = 1 hora
- **Query**: `deleteByExpiraEmBefore(LocalDateTime)`

## 📸 Telegram com Imagens

### Antes (Texto Puro)
```
🔥 iPhone 16
💰 R$3999
📝 Descrição...
```

### Agora (Com Foto)
```
[FOTO DO PRODUTO]
🔥 iPhone 16
💰 R$3999
📝 Descrição reescrita pelo Groq
🛒 Comprar aqui [Link]
```

### Implementação
- **Endpoint**: `/sendPhoto` (não `/sendMessage`)
- **Caption**: HTML formatado (negrito, links, emojis)
- **Fallback**: Se foto quebrar, envia texto simples

## 🏷️ Sistema de Afiliados

### Configuração
- **ID Atual**: `promubr-20`
- **Local**: `PromocaoService.java` linha 17

### Como Funciona
1. Se `adicionarAfiliado: true` → adiciona `?tag=promubr-20` automaticamente
2. Se `adicionarAfiliado: false` → mantém link original
3. Se link já tem `amzn.to` ou `tag=` → não modifica (já tem afiliado)

### Exemplos
```
Input:  https://amazon.com.br/dp/B08N5WRWNW
Output: https://amazon.com.br/dp/B08N5WRWNW?tag=promubr-20

Input:  https://amzn.to/123abc
Output: https://amzn.to/123abc (não modifica, já tem)

Input:  https://amazon.com/dp/xyz?ref=something
Output: https://amazon.com/dp/xyz?ref=something&tag=promubr-20
```

## 🐛 Troubleshooting e Debug

### Verificar Logs do Groq
Se a descrição não for reescrita, procure por:
```
📡 Enviando para Groq...
📝 Prompt: ...
✓ Status Groq: 200
✅ Groq gerou com sucesso: [TEXTO AQUI]
```

Se aparecer:
```
❌ Erro ao chamar Groq API: [ERRO]
⚠️ Groq retornou algo inesperado
```

**Soluções:**
1. Verificar se `GROQ_API_KEY` está correto no `.env`
2. Verificar conexão de internet
3. Verificar se a chave Groq não expirou

### Verificar Logs do Telegram
Procure por:
```
📸 Preparando para enviar foto: [URL]
� Título: [TÍTULO]
💰 Preço: [PREÇO]
🔗 Link: [LINK]
🤖 Bot Token: [TOKEN...]
💬 Chat ID: [CHAT_ID]
🚀 Enviando para: https://api.telegram.org/...
📦 Caption: [CAPTION]
✅ Foto enviada ao Telegram! Status: 200
📤 Response completa: {"ok":true,"result":{...}}
🎉 Mensagem enviada com sucesso!
```

Se aparecer:
```
❌ Erro ao enviar foto ao Telegram: [ERRO]
⚠️ Telegram retornou ok:false
```

**Possíveis causas:**
1. **Token inválido**: Bot token começa com número (ex: `123456:ABC...`)
2. **Chat ID errado**: Grupos começam com `-` (ex: `-1001234567890`)
3. **Bot não é admin**: Bot precisa ser admin do grupo/canal
4. **Imagem URL quebrada**: Verificar se URL da imagem está acessível
5. **Rate limit**: Telegram limita mensagens por segundo

### Testar Conexão Básica
```bash
# Teste simples de mensagem
curl http://localhost:8085/teste/telegram
```

Se funcionar, o problema é na imagem. Se não, é no token/chat ID.

### Ver Logs Completos
```bash
# Se Docker
docker compose logs api -f

# Se local
java -jar target/amazon-promos-1.0.0.jar 2>&1 | tee app.log
```

## 🏗️ Estrutura de Pastas

```
src/main/java/com/promos/app/
├── PromoApplication.java           # Main da app
├── config/
│   └── CorsConfig.java             # CORS configurado
├── controller/
│   ├── ManualController.java        # POST /manual
│   ├── EnviarController.java        # POST /enviar (com logs)
│   ├── EnviarSiteController.java    # GET /enviarsite
│   ├── BancoController.java         # GET /banco, DELETE /banco/{id}
│   └── TesteController.java         # Endpoints de teste
├── dto/
│   └── PromocaoRequest.java         # Sem Lombok (getters/setters manuais)
├── entity/
│   └── Promocao.java                # JPA Entity (sem Lombok)
├── repository/
│   └── PromocaoRepository.java      # Query customizadas
├── scheduler/
│   └── PromocaoScheduler.java       # Delete automático a cada hora
└── service/
    ├── PromocaoService.java         # Lógica de afiliados
    ├── GroqService.java             # Integração Groq com logs
    └── TelegramService.java         # Envio de foto + caption
```

## 📝 Exemplo Completo de Fluxo

```bash
# 1. Criar produto
curl -X POST http://localhost:8085/manual \
  -H 'Content-Type: application/json' \
  -d '{"titulo":"iPhone","preco":"R$4000","link":"https://amazon.com/dp/B123","imagem":"https://img.url","descricaoOriginal":"Specs técnicas...","adicionarAfiliado":true}'

# Resposta: ID 1, descrição reescrita por Groq, link com tag=promubr-20

# 2. Listar todas as promoções
curl http://localhost:8085/banco

# 3. Enviar para Telegram (com foto!)
curl -X POST http://localhost:8085/enviar

# Resultado: Telegram recebe [FOTO] + Título + Preço + Descrição bonita + Link

# 4. Deletar manualmente se precisar
curl -X DELETE http://localhost:8085/banco/1

# 5. Após 24h, sistema deleta automaticamente via scheduler
```

## 🔧 Configurações Importantes

### `application.yml`
- PostgreSQL: `jdbc:postgresql://postgres:5432/amazonpromos`
- Server Port: `8080` (local) ou `8090` (Docker mapeado para 8085)
- DDL Auto: `update` (cria tabelas automaticamente)
- SQL Show: `true` (mostra queries nos logs)

### `docker-compose.yml`
- PostgreSQL: porta 5432
- API: porta 8085 → 8080 (interno)
- Health Check: PostgreSQL aguarda estar pronto antes de API iniciar
- Volumes: `postgres_data` (persistência)

### `pom.xml`
- Spring Boot 3.3.0
- Spring Data JPA
- PostgreSQL Driver
- Sem Lombok (removido para estabilidade de build)

## ⚙️ Variáveis de Ambiente Avançadas

Se quiser customizar:

```env
# Spring Boot
SERVER_PORT=8090
MANAGEMENT_SERVER_PORT=8090

# Banco
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/amazonpromos
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=senha
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Groq (opcional, pode deixar vazio para fallback)
GROQ_API_KEY=

# Telegram
TELEGRAM_BOT_TOKEN=
TELEGRAM_CHAT_ID=
```

## 🎯 Próximas Melhorias Sugeridas

- [ ] Autenticação/API Key para proteger endpoints
- [ ] Rate limiting para Groq (custo)
- [ ] Fila de mensagens para Telegram (resiliência)
- [ ] Dashboard admin para gerenciar promoções
- [ ] Histórico de erros/tentativas de envio
- [ ] Cache de descrições geradas (economizar Groq)

## 🆕 O Que Foi Criado e Acrescentado

### Nova Coluna `precoPromo`
- **Adicionado em**: `Promocao.java` (entidade) e `PromocaoRequest.java` (DTO)
- **Propósito**: Separar valor primário (`preco`) do valor promocional (`precoPromo`)
- **Mapeamento**: `@Column(name = "precoPromo")` para coluna no banco
- **Uso**: Ao cadastrar via `POST /manual`, ambos os campos são salvos
- **Exemplo**: `preco: "R$ 3.999,00"` (valor original), `precoPromo: "R$ 2.999,00"` (promoção)

### Correção de Mapeamento de Colunas
- **Problema inicial**: Campo `preco` mapeado para coluna `"precoInt"` (incorreto)
- **Correção**: Alterado para `@Column(name = "preco")` para coluna `"preco"`
- **Impacto**: Hibernate atualiza o esquema automaticamente com `ddl-auto: update`

### Controllers e Endpoints
- **ManualController**: `POST /manual` para cadastrar promoções
- **EnviarController**: `POST /enviar` (último produto) e `POST /enviar/todos` (todos ativos)
- **BancoController**: `GET /banco` (listar todas) e `DELETE /banco/{id}` (deletar por ID)
- **EnviarSiteController**: `GET /enviarsite` (listar ativas para site)
- **TesteController**: `GET /teste/telegram` (teste de conexão Telegram)

### Services
- **PromocaoService**: Salvar, buscar ativas/todas, deletar
- **TelegramService**: Enviar foto + caption para Telegram
- **GroqService**: Reescrever descrições com IA

### Repository
- **PromocaoRepository**: Extends `JpaRepository`, com queries `@Query` para ativas e delete por expiração

### Scheduler
- **PromocaoScheduler**: `@Scheduled` para deletar promoções expiradas a cada hora

### Configuração Docker
- **Dockerfile**: Build com Maven 3.9.6 + Java 21
- **docker-compose.yml**: PostgreSQL 16 + API, com healthcheck e volumes

## 🚨 Problemas Enfrentados e Soluções

### 1. Erro 404 em `/telegram/enviar`
- **Problema**: Rota não existe; código tem `POST /enviar` sem prefixo `/telegram`
- **Solução**: Usar `POST /enviar` em vez de `/telegram/enviar`
- **Impacto**: Endpoint funciona corretamente para envio ao Telegram

### 2. Mapeamento Incorreto de Colunas no Banco
- **Problema**: Campo `preco` mapeado para `"precoInt"` em vez de `"preco"`
- **Solução**: Corrigido para `@Column(name = "preco")` em `Promocao.java`
- **Impacto**: Colunas `preco` e `precoPromo` agora corretas no PostgreSQL

### 3. Possíveis Erros no Docker
- **Problema**: Build falha se variáveis de ambiente não estiverem definidas
- **Solução**: Garantir que `.env` existe com `POSTGRES_PASSWORD`, `GROQ_API_KEY`, `TELEGRAM_BOT_TOKEN`, `TELEGRAM_CHAT_ID`
- **Verificação**: Rodar `docker compose up --build` e checar logs com `docker compose logs api`

### 4. Lombok Removido
- **Problema**: Dependência Lombok causava conflitos de build
- **Solução**: Removido Lombok; implementados getters/setters manuais em todas as classes
- **Impacto**: Build estável sem dependências externas desnecessárias

### 5. Expiração de Promoções
- **Problema**: Promoções acumulavam no banco sem limpeza
- **Solução**: Scheduler automático para deletar expiradas a cada hora
- **Impacto**: Banco limpo automaticamente, evitando crescimento desnecessário

## 📞 Suporte

Qualquer dúvida, procure pelos logs:
```bash
✓ = sucesso
❌ = erro
⚠️ = aviso
```
📡 = operação
📸 = imagem
```

---

**Última atualização**: 1º de maio de 2026
**Versão**: 1.0.0
**Status**: ✅ Production Ready