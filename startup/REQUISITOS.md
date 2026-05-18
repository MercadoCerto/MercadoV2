# MercadoCerto — Requisitos Funcionais e Não Funcionais

> Documento extraído do estado atual do projeto (backend Spring Boot + frontend HTML/JS).
> Cada requisito aponta o artefato do código que o implementa, quando aplicável.

---

## Sumário

1. [Escopo](#1-escopo)
2. [Atores do sistema](#2-atores-do-sistema)
3. [Requisitos funcionais (RF)](#3-requisitos-funcionais-rf)
4. [Requisitos não funcionais (RNF)](#4-requisitos-não-funcionais-rnf)
5. [Regras de negócio (RN)](#5-regras-de-negócio-rn)
6. [Requisitos de dados](#6-requisitos-de-dados)
7. [Matriz de rastreabilidade](#7-matriz-de-rastreabilidade)

---

## 1. Escopo

O MercadoCerto é uma plataforma web de comparação de preços e reputação de mercados. Atende dois perfis distintos (consumidor e comerciante) e oferece ferramentas de decisão de compra baseadas em dados colaborativos.

**Fora do escopo** (explícito): pagamentos online, entrega, integração com cupons fiscais, aplicativo mobile nativo.

---

## 2. Atores do sistema

| Ator | Tipo de conta | Responsabilidades principais |
|---|---|---|
| **Consumidor** | `USUARIO` | Comparar preços, avaliar mercados, montar listas, visualizar mapa |
| **Comerciante** | `COMERCIO` (+CNPJ) | Cadastrar mercado, produtos e preços, acompanhar reputação no painel |
| **Visitante** | sem conta | Pode navegar e consultar algumas páginas públicas (limitado) |

---

## 3. Requisitos funcionais (RF)

### 3.1. Autenticação e contas

| Código | Requisito | Implementação |
|---|---|---|
| RF01 | O sistema deve permitir cadastro de consumidores e comerciantes com diferenciação via `tipoConta`. | `POST /api/usuarios/register` |
| RF02 | O sistema deve permitir login por `login` + `senha`, com validação segura. | `POST /api/usuarios/login` (BCrypt) |
| RF03 | O sistema deve manter sessão no cliente via `localStorage('mc_usuario')`. | Frontend — sem JWT, sem sessão server-side |
| RF04 | O sistema deve exigir CNPJ para contas do tipo `COMERCIO`. | Campo `cnpj` no model `Usuario` |
| RF05 | O sistema deve impedir reutilização de e-mail, login, CPF e CNPJ. | Constraints `UNIQUE` no banco |
| RF06 | O sistema deve nunca retornar a senha em respostas da API. | `@JsonProperty(access = WRITE_ONLY)` |

### 3.2. Mercados

| Código | Requisito | Implementação |
|---|---|---|
| RF07 | O sistema deve permitir cadastro, edição e remoção de mercados. | `POST/PUT/DELETE /api/mercados` |
| RF08 | O sistema deve armazenar coordenadas GPS (latitude/longitude) de cada mercado. | Campos `latitude`, `longitude` em `mercado` |
| RF09 | O sistema deve permitir busca de mercados próximos dentro de um raio em km. | `GET /api/mercados/proximos?latitude=&longitude=&raioKm=` |
| RF10 | O sistema deve exibir mercados em mapa interativo. | Frontend com Leaflet.js + OpenStreetMap |

### 3.3. Produtos e preços

| Código | Requisito | Implementação |
|---|---|---|
| RF11 | Comerciantes devem poder cadastrar produtos vinculando-os a um mercado e incluindo imagem. | `POST /api/produtos/cadastrar` (multipart/form-data) |
| RF12 | O sistema deve permitir publicação de preços com vínculo a produto + mercado + valor. | `POST /api/precos` |
| RF13 | O sistema deve registrar automaticamente a data/hora de cada preço publicado. | `@PrePersist` em `Preco` |
| RF14 | O sistema deve permitir comparação de preços de um produto entre mercados, retornando ranking. | `GET /api/precos/comparar?produto={id}` |
| RF15 | O sistema deve exibir histórico de preços de um produto em um mercado. | `GET /api/precos/historico?produto=&mercado=` |
| RF16 | O sistema deve exibir catálogo completo de preços vigentes de um mercado. | `GET /api/precos/mercado/{idMercado}` |
| RF17 | O sistema deve exibir o histórico em gráfico (Chart.js). | Frontend — página de histórico |

### 3.4. Avaliações e reputação

| Código | Requisito | Implementação |
|---|---|---|
| RF18 | O sistema deve permitir avaliação de mercados com nota (1 a 5) e comentário opcional. | `POST /api/avaliacoes` |
| RF19 | O sistema deve permitir avaliações anônimas (sem `idUsuario`). | Campo `idUsuario` é nullable |
| RF20 | O sistema deve calcular e exibir a média de notas por mercado. | `GET /api/avaliacoes/media/{idMercado}` |
| RF21 | O sistema deve exibir total de avaliações por mercado. | `GET /api/avaliacoes/total/{idMercado}` |
| RF22 | O sistema deve gerar ranking de mercados ordenado por reputação. | `GET /api/avaliacoes/ranking` |
| RF23 | O sistema deve listar todas as avaliações de um mercado específico. | `GET /api/avaliacoes/mercado/{idMercado}` |

### 3.5. Listas de compras

| Código | Requisito | Implementação |
|---|---|---|
| RF24 | O consumidor deve poder criar, renomear e excluir listas de compras. | `POST/DELETE /api/lista` |
| RF25 | O consumidor deve poder adicionar e remover itens (produto + quantidade) de uma lista. | `POST/DELETE /api/lista/{id}/item` |
| RF26 | O sistema deve listar todas as listas de um consumidor. | `GET /api/lista/usuario/{idUsuario}` |
| RF27 | O sistema deve otimizar a lista calculando a combinação de mercados de menor custo total. | `GET /api/lista/{id}/otimizar` |
| RF28 | O sistema deve comparar o plano otimizado com o plano de mercado único mais barato, exibindo a economia em reais. | Campo `economia` em `ResultadoOtimizadorDTO` |

### 3.6. Painel do comércio

| Código | Requisito | Implementação |
|---|---|---|
| RF29 | O comerciante deve ter acesso a um dashboard com média geral, total de avaliações e de produtos. | `GET /api/painel/dashboard/{idMercado}` |
| RF30 | O dashboard deve exibir evolução mensal das avaliações (média e total por mês). | Campo `evolucaoMensal` em `DashboardComercioDTO` |
| RF31 | O dashboard deve listar as avaliações mais recentes. | Campo `avaliacoesRecentes` em `DashboardComercioDTO` |

### 3.7. Localização

| Código | Requisito | Implementação |
|---|---|---|
| RF32 | O sistema deve permitir registro de endereço de usuário (opcional). | Relação `usuario → endereco → cidade → pais` |
| RF33 | O sistema deve usar geolocalização do navegador para sugerir mercados próximos. | Frontend — API `navigator.geolocation` |

---

## 4. Requisitos não funcionais (RNF)

### 4.1. Segurança

| Código | Requisito | Nível | Implementação atual |
|---|---|---|---|
| RNF01 | Senhas devem ser armazenadas com hash seguro (BCrypt). | Obrigatório | Spring Security — `BCryptPasswordEncoder` |
| RNF02 | A API nunca deve expor a senha em resposta. | Obrigatório | `@JsonProperty(WRITE_ONLY)` no model |
| RNF03 | Dados pessoais (CPF, CNPJ, e-mail) devem ter unicidade garantida. | Obrigatório | Constraints `UNIQUE` |
| RNF04 | Uploads devem ter limite de tamanho. | Obrigatório | `spring.servlet.multipart.max-file-size = 10MB` |
| RNF05 | Endpoints de mutação devem ser restritos a usuários autenticados (a ser endurecido). | A evoluir | Verificação hoje é feita no cliente — roadmap: mover para filtro server-side |

### 4.2. Desempenho

| Código | Requisito | Métrica |
|---|---|---|
| RNF06 | Consultas típicas (comparação, listagem, ranking) devem responder em ≤ 2s com até 10 mil registros. | p95 ≤ 2s |
| RNF07 | A tela de comparação deve renderizar ranking em ≤ 1s após resposta do backend. | p95 ≤ 1s |
| RNF08 | A otimização de listas com até 50 itens deve responder em ≤ 3s. | p95 ≤ 3s |

### 4.3. Usabilidade

| Código | Requisito | Critério |
|---|---|---|
| RNF09 | A interface deve ser responsiva (desktop, tablet, mobile). | CSS Grid + Flexbox — testado ≥ 360px |
| RNF10 | A paleta deve manter identidade visual consistente (roxo #4b0082 primário; Syne e DM Sans). | Definido no design system |
| RNF11 | O sistema deve fornecer feedback visual para ações (toasts, spinners, modais). | Já implementado em todas as páginas |
| RNF12 | Mensagens de erro devem ser compreensíveis para usuários não-técnicos. | A revisar sprint 2 |

### 4.4. Compatibilidade

| Código | Requisito | Cobertura |
|---|---|---|
| RNF13 | O frontend deve funcionar nas versões atuais de Chrome, Firefox, Edge e Safari. | Últimas 2 versões |
| RNF14 | O backend deve rodar em Java 17+ com Maven 3.9+. | Dependências fixadas no `pom.xml` |
| RNF15 | O banco deve ser MariaDB 10.x+ (compatível com MySQL 8). | Driver `mariadb-java-client` |

### 4.5. Manutenibilidade e evolução

| Código | Requisito | Como é atendido |
|---|---|---|
| RNF16 | O código deve seguir arquitetura em camadas (Controller → Service → Repository). | Padrão Spring Boot já aplicado |
| RNF17 | Entidades devem usar Lombok para reduzir boilerplate. | `@Data`, `@Builder` aplicados |
| RNF18 | DTOs devem desacoplar a API dos models (evitar vazamento de schema). | Pasta `dto/` já em uso |
| RNF19 | O projeto deve estar versionado em Git (GitHub) para toda a equipe. | **Pendente** — item de sprint 1 |

### 4.6. Portabilidade e implantação

| Código | Requisito | Critério |
|---|---|---|
| RNF20 | O sistema deve rodar em qualquer SO que suporte JVM 17+ (Windows, Linux, macOS). | OK — Spring Boot standalone |
| RNF21 | O banco deve ser inicializável via script SQL versionado. | `mercdadocertocreate.sql`, `migrar_banco.sql` |
| RNF22 | A configuração de conexão deve ser parametrizável por `application.properties`. | Já implementado |

### 4.7. Observabilidade

| Código | Requisito | Implementação |
|---|---|---|
| RNF23 | Queries SQL devem poder ser inspecionadas em desenvolvimento. | `spring.jpa.show-sql=true` |
| RNF24 | Logs devem distinguir níveis (INFO, WARN, ERROR). | Logback padrão do Spring Boot |

### 4.8. Confiabilidade

| Código | Requisito | Critério |
|---|---|---|
| RNF25 | A integridade referencial entre tabelas deve ser garantida no banco. | Foreign keys + `CASCADE` em `item_lista` |
| RNF26 | Exclusão de listas deve remover itens em cascata. | `ON DELETE CASCADE` em `item_lista` |
| RNF27 | Preços históricos devem ser preservados (cada publicação é um novo registro). | Sem UPDATE na tabela `preco` |

---

## 5. Regras de negócio (RN)

Resumo consolidado, referenciando os processos modelados em `MODELAGEM_PROCESSOS.md`.

| Código | Regra |
|---|---|
| RN01 | Só é exibido o preço mais recente por mercado na comparação. |
| RN02 | Preços são ordenados crescentemente por valor. |
| RN03 | A data/hora do preço deve ser exibida para transparência. |
| RN04 | Apenas contas `COMERCIO` podem publicar preços. |
| RN05 | Cada publicação cria um novo registro em `preco` — histórico preservado. |
| RN06 | O campo `dataHora` é preenchido automaticamente pelo backend. |
| RN07 | Avaliações podem ser anônimas. |
| RN08 | Ranking considera média e total de votos. |
| RN09 | O dashboard mostra evolução mensal das notas. |
| RN10 | Otimização de lista compara "melhor caminho otimizado" × "mercado único mais barato". |
| RN11 | A economia é `totalMercadoMaisBarato − totalOtimizado`. |
| RN12 | Itens sem preço em nenhum mercado são listados à parte. |

---

## 6. Requisitos de dados

Entidades principais (ver detalhes no `DOCUMENTACAO.md`):

| Entidade | Chave primária | Relacionamentos |
|---|---|---|
| `usuario` | `id_usuario` | 1:N com `lista_compras`, 1:N com `avaliacao` |
| `mercado` | `id_mercado` | 1:N com `preco`, 1:N com `avaliacao` |
| `produto` | `id_produto` | 1:N com `preco` |
| `preco` | `id_preco` | N:1 para `produto`, N:1 para `mercado` |
| `avaliacao` | `id_avaliacao` | N:1 para `mercado`, N:1 para `usuario` (nullable) |
| `lista_compras` | `id_lista` | N:1 para `usuario`, 1:N com `item_lista` |
| `item_lista` | `id_item` | N:1 para `lista_compras` (CASCADE) |
| `endereco`, `cidade`, `pais` | — | Hierarquia auxiliar para localização |

---

## 7. Matriz de rastreabilidade

Exemplo parcial — cruzamento entre requisitos, processos BPMN e endpoints.

| Processo BPMN | RFs atendidos | Endpoints |
|---|---|---|
| Comparar preços | RF14, RF17 | `GET /api/precos/comparar`, `GET /api/precos/historico` |
| Cadastrar/atualizar preço | RF11, RF12, RF13 | `POST /api/produtos/cadastrar`, `POST /api/precos` |
| Avaliar mercado | RF18, RF19, RF20, RF21, RF22, RF23 | `POST /api/avaliacoes`, `GET /api/avaliacoes/*` |
| Lista otimizada | RF24, RF25, RF26, RF27, RF28 | `POST/DELETE /api/lista`, `GET /api/lista/{id}/otimizar` |
| Dashboard comércio | RF29, RF30, RF31 | `GET /api/painel/dashboard/{idMercado}` |
