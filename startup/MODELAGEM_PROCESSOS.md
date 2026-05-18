# MercadoCerto — Modelagem de Processos de Negócio (BPMN)

> **Importante**: este documento descreve **processos de negócio**, não casos de uso.
> Foco nos fluxos reais que a plataforma automatiza/apoia, com atores, atividades, decisões e artefatos.
> Para diagramar: usar [bpmn.io](https://bpmn.io) (gratuito, exporta PNG/SVG).

---

## Sumário

1. [Atores e sistemas](#1-atores-e-sistemas)
2. [Processo 1 — Comparar preços de um produto](#2-processo-1--comparar-preços-de-um-produto)
3. [Processo 2 — Cadastrar/atualizar preço (comerciante)](#3-processo-2--cadastraratualizar-preço-comerciante)
4. [Processo 3 — Avaliar mercado](#4-processo-3--avaliar-mercado)
5. [Processo 4 — Montar lista de compras otimizada](#5-processo-4--montar-lista-de-compras-otimizada)
6. [Processos de apoio](#6-processos-de-apoio)
7. [Convenções de notação](#7-convenções-de-notação)

---

## 1. Atores e sistemas

### Pools (raias externas)
- **Consumidor** — usuário final que compara preços, avalia mercados e monta listas.
- **Comerciante** — representante de um mercado (conta `COMERCIO`) que publica preços e acompanha reputação.

### Lanes (raias internas do sistema MercadoCerto)
- **Frontend Web** — páginas HTML/JS (consumidor e painel do comércio).
- **Backend (API REST Spring Boot)** — Controllers + Services.
- **Banco de dados (MariaDB)** — persistência.

### Sistemas externos
- **OpenStreetMap (Leaflet)** — renderização do mapa.
- **Geolocalização do navegador** — obtenção da posição do consumidor.

---

## 2. Processo 1 — Comparar preços de um produto

### Objetivo de negócio
Permitir ao consumidor descobrir, em poucos segundos, qual mercado pratica o menor preço de um produto específico.

### Gatilho (evento de início)
Consumidor acessa a página de comparação e digita/seleciona um produto.

### Atividades

| # | Raia | Atividade | Tipo |
|---|---|---|---|
| 1 | Consumidor | Informa produto de interesse | Tarefa de usuário |
| 2 | Frontend | Envia `GET /api/precos/comparar?produto={id}` | Tarefa de serviço |
| 3 | Backend | Busca todos os registros de `preco` do produto | Tarefa de serviço |
| 4 | Backend | Para cada mercado, seleciona o preço **mais recente** | Tarefa de serviço |
| 5 | Backend | Ordena resultado do menor para o maior | Tarefa de serviço |
| 6 | Backend | Retorna `ComparadorDTO` com lista de preços | Tarefa de serviço |
| 7 | Frontend | Renderiza ranking com nome do mercado, valor e data | Tarefa de usuário |
| 8 | Consumidor | Analisa e decide (evento final) | — |

### Gateway (decisão)
Após passo 3: **existe algum preço cadastrado para o produto?**
- **Sim** → segue para o passo 4.
- **Não** → Backend retorna lista vazia → Frontend exibe mensagem "Nenhum preço encontrado" → fim alternativo.

### Evento de fim
- **Sucesso**: consumidor visualiza ranking comparativo.
- **Alternativo**: nenhum preço disponível.

### Regras de negócio
- RN01: Só é exibido o preço mais recente por mercado (evita histórico "poluído").
- RN02: Preços são ordenados crescentemente por valor.
- RN03: A data/hora do preço deve ser exibida para transparência.

### Diagrama BPMN (texto para bpmn.io)
```
[Início: consumidor busca produto]
        ↓
[Atividade: Frontend → GET /api/precos/comparar]
        ↓
[Atividade: Backend busca preços no banco]
        ↓
<Gateway exclusivo: existem preços?>
   ├─ Não → [Mensagem "sem preços"] → (Fim alternativo)
   └─ Sim → [Filtrar preço mais recente por mercado]
                ↓
            [Ordenar ascendente]
                ↓
            [Retornar ComparadorDTO]
                ↓
            [Frontend renderiza ranking]
                ↓
            (Fim de sucesso)
```

---

## 3. Processo 2 — Cadastrar/atualizar preço (comerciante)

### Objetivo de negócio
Manter a base de preços do MercadoCerto atualizada, permitindo ao comerciante publicar valores vigentes em seu estabelecimento.

### Gatilho
Comerciante detecta mudança de preço (promoção, reajuste, novo produto) e acessa o painel.

### Atividades

| # | Raia | Atividade | Tipo |
|---|---|---|---|
| 1 | Comerciante | Efetua login (`POST /api/usuarios/login`) | Tarefa de usuário |
| 2 | Backend | Valida senha com BCrypt | Tarefa de serviço |
| 3 | Frontend | Armazena sessão em `localStorage('mc_usuario')` | Tarefa de serviço |
| 4 | Comerciante | Acessa painel do comércio | Tarefa de usuário |
| 5 | Comerciante | Escolhe: cadastrar produto novo **OU** atualizar preço de existente | Tarefa de usuário |
| 6a | Frontend | (Novo) Envia `POST /api/produtos/cadastrar` (multipart: produto + imagem + idMercado) | Tarefa de serviço |
| 6b | Frontend | (Atualização) Envia `POST /api/precos` com `{idProduto, idMercado, valor}` | Tarefa de serviço |
| 7 | Backend | Persiste em `produto` e/ou `preco` (com `@PrePersist` definindo `dataHora`) | Tarefa de serviço |
| 8 | Backend | Retorna confirmação | Tarefa de serviço |
| 9 | Frontend | Exibe toast de sucesso | Tarefa de usuário |

### Gateways
- **Após passo 2**: autenticação OK?
  - Não → mensagem de erro → fim alternativo.
  - Sim → prossegue.
- **Após passo 2 (paralelo)**: tipo da conta é `COMERCIO`?
  - Não → acesso ao painel é bloqueado → fim alternativo.
  - Sim → prossegue.
- **No passo 5**: produto já existe no catálogo?
  - Não → caminho 6a (cadastro completo).
  - Sim → caminho 6b (apenas novo preço).

### Evento de fim
- **Sucesso**: novo preço disponível para os consumidores em tempo real.
- **Alternativo**: falha de autenticação ou permissão.

### Regras de negócio
- RN04: Apenas contas do tipo `COMERCIO` podem publicar preços.
- RN05: Cada publicação cria um **novo registro** em `preco` — o histórico é preservado.
- RN06: O campo `dataHora` é preenchido automaticamente pelo backend.

---

## 4. Processo 3 — Avaliar mercado

### Objetivo de negócio
Gerar reputação confiável dos mercados a partir de feedback real de consumidores, alimentando o ranking e o dashboard do comércio.

### Gatilho
Consumidor termina uma compra (ou experiência) e quer opinar sobre o mercado.

### Atividades

| # | Raia | Atividade | Tipo |
|---|---|---|---|
| 1 | Consumidor | Abre a ficha do mercado | Tarefa de usuário |
| 2 | Frontend | Carrega `GET /api/avaliacoes/mercado/{id}` e `GET /api/avaliacoes/media/{id}` | Tarefa de serviço |
| 3 | Consumidor | Clica em "Avaliar" e preenche nota (1–5) e comentário opcional | Tarefa de usuário |
| 4 | Frontend | Envia `POST /api/avaliacoes` com `{idMercado, nota, comentario?, dataAvaliacao}` | Tarefa de serviço |
| 5 | Backend | Valida nota no intervalo 1–5 (`CHECK` no banco) | Tarefa de serviço |
| 6 | Backend | Persiste em `avaliacao` (com `idUsuario` ou NULL para anônimo) | Tarefa de serviço |
| 7 | Backend | Recalcula média agregada do mercado (on-demand quando consultada) | Tarefa de serviço |
| 8 | Frontend | Atualiza média e lista de avaliações na tela | Tarefa de serviço |
| 9 | Comerciante | (Evento interceptor) Vê nova avaliação no dashboard | Evento |

### Gateway
- **No passo 5**: nota está entre 1 e 5?
  - Não → erro 400 → frontend exibe "nota inválida" → fim alternativo.
  - Sim → persiste.

### Evento de fim
- **Sucesso**: avaliação registrada, ranking e dashboard do comerciante refletem a mudança na próxima consulta.
- **Alternativo**: nota inválida ou erro de rede.

### Regras de negócio
- RN07: Avaliações podem ser anônimas (`idUsuario` nulo).
- RN08: Ranking (`GET /api/avaliacoes/ranking`) considera média e total de votos.
- RN09: O dashboard do comerciante mostra evolução mensal das notas.

---

## 5. Processo 4 — Montar lista de compras otimizada

### Objetivo de negócio
Entregar ao consumidor o menor custo total para um conjunto de produtos, distribuindo a compra entre múltiplos mercados quando vantajoso.

### Gatilho
Consumidor precisa fazer compras e quer economizar.

### Atividades

| # | Raia | Atividade | Tipo |
|---|---|---|---|
| 1 | Consumidor | Cria lista (`POST /api/lista?idUsuario={id}&nomeLista={nome}`) | Tarefa de usuário |
| 2 | Consumidor | Adiciona produtos e quantidades (`POST /api/lista/{id}/item`) | Tarefa de usuário |
| 3 | Consumidor | Aciona "Otimizar lista" | Tarefa de usuário |
| 4 | Frontend | Chama `GET /api/lista/{id}/otimizar` | Tarefa de serviço |
| 5 | Backend | Para cada item, busca menor preço vigente em cada mercado | Tarefa de serviço |
| 6 | Backend | Calcula: total por mercado único **vs.** total otimizado (split entre mercados) | Tarefa de serviço |
| 7 | Backend | Monta `ResultadoOtimizadorDTO` com planos, subtotal e economia | Tarefa de serviço |
| 8 | Frontend | Exibe plano otimizado, comparação e mapa dos mercados | Tarefa de usuário |
| 9 | Consumidor | Decide: seguir plano otimizado ou plano de mercado único | Tarefa de usuário |

### Gateway
- **No passo 5**: há preços cadastrados para todos os itens?
  - Não → itens sem preço são marcados; o cálculo prossegue com os demais.
  - Sim → cálculo completo.

### Evento de fim
- **Sucesso**: consumidor recebe plano com valor economizado em reais.
- **Alternativo**: lista vazia ou nenhum item com preço → mensagem orientativa.

### Regras de negócio
- RN10: O algoritmo sempre compara o "melhor caminho otimizado" com o "melhor mercado único".
- RN11: A economia exibida é `totalMercadoMaisBarato − totalOtimizado`.
- RN12: Itens sem preço em nenhum mercado são listados à parte.

---

## 6. Processos de apoio

Processos secundários que não foram detalhados mas também compõem o escopo:

| Processo | Descrição resumida |
|---|---|
| Cadastro de consumidor | `POST /api/usuarios/register` com `tipoConta=USUARIO`. |
| Cadastro de comerciante | `POST /api/usuarios/register` com `tipoConta=COMERCIO` + CNPJ. |
| Busca de mercados próximos | `GET /api/mercados/proximos?latitude=&longitude=&raioKm=` — usa geolocalização do navegador. |
| Consulta de histórico de preços | `GET /api/precos/historico?produto=&mercado=` — alimenta gráfico Chart.js. |
| Visualização de dashboard do comércio | `GET /api/painel/dashboard/{idMercado}` — KPIs e evolução mensal. |

---

## 7. Convenções de notação

Para diagramar no [bpmn.io](https://bpmn.io), usar:

| Elemento | Quando |
|---|---|
| **Evento de início** (círculo fino) | Gatilho externo (usuário clica, hora chega) |
| **Atividade** (retângulo arredondado) | Ação concreta feita por um ator ou sistema |
| **Gateway exclusivo** (losango com "×") | Decisão binária/múltipla exclusiva |
| **Gateway paralelo** (losango com "+") | Ramos executados em paralelo |
| **Evento de fim** (círculo grosso) | Conclusão (sucesso ou alternativo) |
| **Pool/Lane** (faixas horizontais) | Separar atores (Consumidor × Backend × DB) |
| **Fluxo de mensagem** (linha tracejada) | Comunicação entre pools (ex.: Frontend → Backend via HTTP) |

### Entregáveis por processo
Para cada um dos 4 processos principais, gerar:
1. Diagrama BPMN em PNG/SVG (exportado do bpmn.io).
2. Descrição textual (já neste documento).
3. Lista de regras de negócio vinculadas (já neste documento).
