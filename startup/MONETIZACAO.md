# MercadoCerto — Modelo de Monetização

> Documento de resposta direta ao feedback da banca: "falta de monetização — como o projeto vai gerar receita?"
> Ponto a ser apresentado no pitch final (22/06).

---

## Sumário

1. [Diagnóstico do problema](#1-diagnóstico-do-problema)
2. [Proposta de valor por público](#2-proposta-de-valor-por-público)
3. [Fluxos de receita candidatos](#3-fluxos-de-receita-candidatos)
4. [Recomendação: modelo híbrido (2 fluxos principais)](#4-recomendação-modelo-híbrido-2-fluxos-principais)
5. [Projeção simplificada](#5-projeção-simplificada)
6. [Roadmap de monetização](#6-roadmap-de-monetização)
7. [Script para o pitch](#7-script-para-o-pitch)

---

## 1. Diagnóstico do problema

O MercadoCerto opera em uma **plataforma de dois lados** (multi-sided market):

- **Lado consumidor**: quer comparar preços, avaliar mercados, economizar na compra mensal.
- **Lado comerciante**: quer visibilidade, clientes novos e dados de reputação.

A regra clássica desse tipo de plataforma é:
> **Quem mais valoriza o acesso paga. Quem gera o tráfego, não paga.**

Para o MercadoCerto, isso significa: **consumidores acessam grátis** (caso contrário não há dados, nem avaliações, nem tráfego). A receita vem dos **comerciantes** e, secundariamente, de **parceiros de mídia**.

---

## 2. Proposta de valor por público

### Para o consumidor
- Economia mensal real (exibida em reais na otimização de listas).
- Transparência de preços.
- Reputação confiável dos mercados (avaliações colaborativas).

### Para o comerciante
- Canal direto para atrair consumidores sensíveis a preço.
- Visibilidade em buscas e comparações.
- Feedback estruturado (dashboard com evolução mensal).
- Dados de mercado: quais produtos têm mais comparação, quais preços são competitivos.

### Para parceiros (marcas/indústrias)
- Segmentação por produto, categoria e geografia.
- Mensuração de reputação de distribuidores.
- Canal de promoção de novos produtos.

---

## 3. Fluxos de receita candidatos

### 3.1. Plano premium para comerciantes (SaaS B2B)

| Atributo | Detalhe |
|---|---|
| **Cliente** | Mercados cadastrados (conta `COMERCIO`) |
| **Modelo** | Assinatura mensal recorrente (MRR) |
| **Tiers propostos** | Grátis / Pro / Enterprise |
| **Plano Grátis** | Cadastro de produtos e preços, recebimento de avaliações |
| **Plano Pro (R$ 49–99/mês)** | Destaque em buscas + dashboard avançado (evolução mensal, comparação com concorrentes) + alertas |
| **Plano Enterprise (R$ 199+/mês)** | Analytics completo + API para integração com ERP + selo "Mercado Verificado" |
| **Força** | Recorrente, escalável, alinhado ao valor percebido |
| **Risco** | Depende de base instalada relevante de consumidores |

### 3.2. Comissão por conversão (performance)

| Atributo | Detalhe |
|---|---|
| **Cliente** | Mercados parceiros |
| **Modelo** | Taxa por lista otimizada que direciona consumidor ao mercado |
| **Valor sugerido** | 1%–3% do ticket da lista, pago pelo mercado receptor |
| **Medição** | Evento "lista enviada para mercado X" no backend |
| **Força** | Zero custo para comerciantes pequenos (só paga se vender) |
| **Risco** | Depende de rastreamento de conversão — pode exigir integração ou confiança |

### 3.3. Publicidade segmentada

| Atributo | Detalhe |
|---|---|
| **Cliente** | Marcas, distribuidores, os próprios mercados |
| **Modelo** | CPM/CPC ou patrocínio fixo de categoria |
| **Formatos** | Banner na home, produto patrocinado no topo do comparador, categoria destacada |
| **Força** | Receita não depende de conversão direta |
| **Risco** | Pode degradar experiência do consumidor se mal dosado |

### 3.4. Assinatura do consumidor (B2C — opcional)

| Atributo | Detalhe |
|---|---|
| **Cliente** | Consumidor fiel |
| **Modelo** | Assinatura enxuta (R$ 9,90/mês) |
| **Benefícios** | Alertas de queda de preço, histórico estendido, sem anúncios |
| **Força** | Diversifica receita, cria engajamento |
| **Risco** | Aceitação baixa em mercado brasileiro sensível a preço |

### 3.5. Dados agregados e anonimizados (B2B)

| Atributo | Detalhe |
|---|---|
| **Cliente** | Indústria, consultorias, pesquisas de mercado |
| **Modelo** | Venda de relatórios mensais (anonimizado e agregado) |
| **Exemplos** | "Evolução do preço médio do arroz no bairro X", "Ranking de reputação por região" |
| **Força** | Receita de alta margem sobre um ativo que o sistema já gera |
| **Risco** | Exige volume significativo e conformidade com LGPD |

---

## 4. Recomendação: modelo híbrido (2 fluxos principais)

Para o pitch final, a recomendação é escolher **2 fluxos com sinergia** e postergar os demais ao roadmap. A combinação sugerida:

### Fluxo principal 1 — Plano premium para comerciantes (SaaS B2B)
- **Por quê**: receita recorrente, previsível e escalável. É o "coração" do negócio.
- **Como funciona**:
  - Plano Grátis captura a base → gera dados.
  - Plano Pro (R$ 49–99/mês) ativa destaque + dashboard avançado.
  - Plano Enterprise (R$ 199+/mês) libera API e selos de verificação.
- **Métricas-chave**: taxa de conversão Grátis → Pro, churn mensal, MRR.

### Fluxo principal 2 — Publicidade segmentada (CPM/patrocínio)
- **Por quê**: começa a gerar receita antes mesmo da base premium amadurecer, usando o tráfego que a comparação naturalmente gera.
- **Como funciona**:
  - Slot "Produto em destaque" no topo do comparador (1 vaga por busca, leilão ou rotação).
  - Banner patrocinado na home e na listagem de mercados próximos.
  - Opção de "Mercado patrocinado do dia".
- **Métricas-chave**: CPM, taxa de clique, receita por sessão.

### Sinergia entre os dois
- Anunciantes entram pela publicidade → percebem valor → assinam plano premium.
- Comerciantes premium ganham desconto/créditos em publicidade.

### Fluxos deixados para o roadmap
- Comissão por conversão (depende de rastreamento maduro).
- Assinatura do consumidor (depende de base ampla).
- Venda de dados agregados (depende de volume + conformidade LGPD).

---

## 5. Projeção simplificada

> Valores ilustrativos para o pitch — não comprometem o modelo final.

### Cenário de partida (ano 1)

| Indicador | Valor estimado |
|---|---|
| Mercados cadastrados | 100 |
| % em plano Pro | 20% (20 mercados) |
| Ticket médio Pro | R$ 79/mês |
| **Receita recorrente Pro** | **R$ 1.580/mês ≈ R$ 19 mil/ano** |
| Receita publicitária média | R$ 500/mês |
| **Receita total estimada ano 1** | **≈ R$ 25 mil** |

### Cenário de escala (ano 3)

| Indicador | Valor estimado |
|---|---|
| Mercados cadastrados | 1.000 |
| % em plano Pro | 25% (250 mercados) |
| Ticket médio Pro | R$ 89/mês |
| Plano Enterprise | 20 mercados × R$ 299/mês |
| **Receita recorrente** | **≈ R$ 28 mil/mês** |
| Receita publicitária | R$ 8 mil/mês |
| **Receita total estimada ano 3** | **≈ R$ 430 mil/ano** |

> **Observação**: os números acima são premissas educadas para a apresentação. Em um plano de negócios real, seriam validados com pesquisa primária e benchmarks do setor (Mercado Livre, iFood, Horti Fruti tech etc.).

---

## 6. Roadmap de monetização

| Fase | Quando | Entregas |
|---|---|---|
| **Fase 0 — MVP (agora)** | Sprints 1–4 / trabalho acadêmico | Plataforma gratuita para todos. Coleta de base e validação das funcionalidades. |
| **Fase 1 — Lançamento Pro** | Pós-banca (3–6 meses) | Lançar plano Pro com 2–3 mercados piloto. Precificação validada com entrevistas. |
| **Fase 2 — Publicidade** | 6–12 meses | Slot de produto patrocinado. Parceria com 1–2 marcas regionais. |
| **Fase 3 — Enterprise + API** | 12–18 meses | Plano Enterprise com integração ERP/PDV. |
| **Fase 4 — Expansão** | 18+ meses | Assinatura do consumidor, venda de dados agregados, expansão geográfica. |

---

## 7. Script para o pitch

Slide sugerido (formato "U Pit", 1 slide):

> **Como o MercadoCerto gera receita?**
>
> **Modelo híbrido B2B:**
>
> **1. Plano premium para mercados (SaaS recorrente)**
> - Grátis → Pro (R$ 79/mês) → Enterprise (R$ 299/mês)
> - Destaque em buscas, dashboard avançado, API
>
> **2. Publicidade segmentada**
> - Produtos patrocinados no comparador
> - Mercado patrocinado do dia
>
> **Por que funciona?**
> - Consumidor acessa grátis → gera tráfego e dados.
> - Mercados pagam pela visibilidade → geram receita recorrente.
> - Marcas pagam pela segmentação → complementam a receita.
>
> **Projeção**: ~R$ 25 mil no ano 1, ~R$ 430 mil no ano 3.

### Perguntas prováveis da banca e respostas

| Pergunta esperada | Resposta |
|---|---|
| "Por que o consumidor não paga?" | Se pagasse, não teríamos base. Sem base, não há dado nem avaliação. Modelo freemium clássico de plataforma de dois lados. |
| "Por que um mercado pagaria R$ 79/mês?" | Porque o custo de aquisição de um cliente sensível a preço por canais tradicionais (folheto, rádio local) é significativamente maior, e sem mensuração. |
| "Como vocês competem com apps de delivery/comparadores existentes?" | Foco em **preço + reputação** no varejo físico local, não delivery. Nicho não coberto adequadamente pelos grandes. |
| "E a LGPD?" | Dados pessoais não são vendidos. Apenas dados agregados e anônimos (e só na fase 4). Termo de uso e política de privacidade desde o MVP. |
