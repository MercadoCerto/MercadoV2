# MercadoCerto — Documentação Arquitetural

> Visão de arquitetura do sistema, extraída do estado atual do código e complementando o `DOCUMENTACAO.md`.
> Foco em: decisões, visões (componentes/implantação), qualidades atendidas e riscos conhecidos.

---

## Sumário

1. [Visão geral da arquitetura](#1-visão-geral-da-arquitetura)
2. [Visão de componentes](#2-visão-de-componentes)
3. [Visão de implantação](#3-visão-de-implantação)
4. [Visão de dados](#4-visão-de-dados)
5. [Fluxos principais (runtime)](#5-fluxos-principais-runtime)
6. [Decisões arquiteturais (ADR resumido)](#6-decisões-arquiteturais-adr-resumido)
7. [Atributos de qualidade](#7-atributos-de-qualidade)
8. [Riscos arquiteturais e dívidas técnicas](#8-riscos-arquiteturais-e-dívidas-técnicas)
9. [Roadmap arquitetural](#9-roadmap-arquitetural)

---

## 1. Visão geral da arquitetura

O MercadoCerto é uma aplicação **web fullstack client-server**, monolítica, organizada em camadas clássicas do Spring Boot. Não há microsserviços, filas, cache distribuído ou autenticação federada.

### Estilo arquitetural
- **Cliente-Servidor** sobre HTTP.
- **Monolito modular em camadas** (Controller → Service → Repository → Banco).
- **Frontend estático** consumindo **API REST** (sem SSR, sem framework SPA).

### Stack
| Camada | Tecnologia |
|---|---|
| Apresentação | HTML5, CSS3, JavaScript (ES6+), Chart.js, Leaflet.js |
| API | Spring Boot 3.3.5 (Java 17), Spring Web |
| Segurança | Spring Security 6.x (BCrypt) |
| Persistência | Spring Data JPA / Hibernate |
| Banco | MariaDB 10.x+ |
| Build | Maven 3.9+ |

---

## 2. Visão de componentes

### Diagrama de componentes

```
┌──────────────────────────────────────────────────────────────────┐
│                       NAVEGADOR (cliente)                        │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │               Páginas HTML + JS (pasta /resources)        │   │
│  │  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌──────────────┐  │   │
│  │  │ Login/  │ │Comparar  │ │Avaliar  │ │Painel do     │  │   │
│  │  │Cadastro │ │ Preços   │ │ Mercado │ │ Comércio     │  │   │
│  │  └─────────┘ └──────────┘ └─────────┘ └──────────────┘  │   │
│  │                                                          │   │
│  │  Dependências externas (CDN):                            │   │
│  │   • Chart.js 4.4.1     • Leaflet.js 1.9.4                │   │
│  │   • Font Awesome 6.5   • Google Fonts                    │   │
│  │                                                          │   │
│  │  Estado local: localStorage('mc_usuario')                │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                        ▲
                        │ HTTP/JSON (fetch API)
                        ▼
┌──────────────────────────────────────────────────────────────────┐
│                  SERVIDOR (Spring Boot 3.3.5)                    │
│                                                                  │
│  ┌────────────────────── Camada Web ──────────────────────────┐ │
│  │ UsuarioController   ProdutoController   MercadoController  │ │
│  │ PrecoController     AvaliacaoController ListaController    │ │
│  │ PainelController                                            │ │
│  └─────────────────────────┬───────────────────────────────────┘ │
│                            │                                     │
│  ┌────────────────────── Camada de Serviço ──────────────────┐  │
│  │ UsuarioService  ProdutoService  MercadoService             │  │
│  │ PrecoService    AvaliacaoService ListaService              │  │
│  │ OtimizadorService  PainelService                           │  │
│  └─────────────────────────┬──────────────────────────────────┘  │
│                            │                                     │
│  ┌────────────────────── Camada de Repositório ──────────────┐  │
│  │ Spring Data JPA (interfaces UsuarioRepository, etc.)       │  │
│  └─────────────────────────┬──────────────────────────────────┘  │
│                            │                                     │
│  ┌────────────────────── Camada de Modelo ───────────────────┐  │
│  │ Entidades JPA: Usuario, Mercado, Produto, Preco,           │  │
│  │ Avaliacao, ListaCompras, ItemLista, Endereco, Cidade, Pais │  │
│  └─────────────────────────┬──────────────────────────────────┘  │
│                            │                                     │
│  ┌────────────────────── Infraestrutura ─────────────────────┐  │
│  │ • Spring Security (BCrypt)                                 │  │
│  │ • Multipart upload (imagens em /uploads)                   │  │
│  │ • Hibernate (ddl-auto = update)                            │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────┬───────────────────────────┘
                                       │ JDBC (MariaDB driver)
                                       ▼
                     ┌─────────────────────────────────┐
                     │        MariaDB 10.x+            │
                     │   Banco: MercadoCerto           │
                     │   (10 tabelas)                  │
                     └─────────────────────────────────┘
```

### Responsabilidades por camada

| Camada | Responsabilidade | Exemplo |
|---|---|---|
| **Controller** | Receber HTTP, validar payload básico, delegar para service, retornar DTO | `PrecoController.comparar()` |
| **Service** | Lógica de negócio, orquestração, cálculos, validações de domínio | `OtimizadorService.otimizar()` |
| **Repository** | Acesso ao banco via Spring Data JPA | `PrecoRepository.findByProduto()` |
| **Model** | Entidade mapeada com JPA (`@Entity`) | `Preco`, `Mercado` |
| **DTO** | Desacopla entrada/saída da API dos models | `ComparadorDTO`, `ResultadoOtimizadorDTO` |

---

## 3. Visão de implantação

### Cenário atual (desenvolvimento local)

```
┌───────────────────────────────────────────────┐
│     Máquina do desenvolvedor (Windows)        │
│                                               │
│  ┌─────────────────────────────────────────┐  │
│  │  JVM 17                                 │  │
│  │  ├── Spring Boot (porta 8080)           │  │
│  │  └── Tomcat embarcado                   │  │
│  └─────────────────────────────────────────┘  │
│                     │                         │
│  ┌─────────────────────────────────────────┐  │
│  │  MariaDB 10.x (porta 3306)              │  │
│  └─────────────────────────────────────────┘  │
│                                               │
│  Pasta /uploads (imagens de produtos)         │
└───────────────────────────────────────────────┘
                    ▲
                    │ HTTP
                    ▼
          Navegador do usuário final
```

**Limitação atual**: banco roda apenas na máquina do David (ponto único de falha). Ver item 8.

### Cenário-alvo (sprint 1 — descentralização)

```
GitHub (código + scripts SQL)
        │
        ▼
Cada membro da equipe clona localmente
        │
        ▼
Cada máquina roda MariaDB local + backend local
        │
        ▼
(futuro) Servidor único com banco compartilhado
        (custo a avaliar)
```

---

## 4. Visão de dados

Modelo de dados detalhado está em `DOCUMENTACAO.md` (seção 5). Resumo:

```
pais ──< cidade ──< endereco
                        │
                        ▲ (opcional)
usuario ──< lista_compras ──< item_lista
   │
   └──< avaliacao >── mercado ──< preco >── produto
```

**Integridade**:
- FKs em todas as relações.
- `CASCADE DELETE` em `item_lista` (quando a lista é removida, itens vão junto).
- `CHECK(nota BETWEEN 1 AND 5)` em `avaliacao`.
- `UNIQUE` em e-mail, login, CPF, CNPJ.

**Histórico**:
- A tabela `preco` é *append-only*: cada publicação é um novo registro. Isso permite histórico e gráficos ao custo de mais linhas.

---

## 5. Fluxos principais (runtime)

### 5.1. Autenticação
```
Cliente                     Backend                  DB
  │   POST /login (login, senha)  │                   │
  │─────────────────────────────→│                   │
  │                               │ SELECT usuario    │
  │                               │──────────────────→│
  │                               │←──────────────────│
  │                               │ BCrypt.matches()  │
  │   LoginResponseDTO            │                   │
  │←─────────────────────────────│                   │
  │ localStorage.set()            │                   │
```

### 5.2. Comparação de preços
```
Cliente → GET /api/precos/comparar?produto=9
       → PrecoController.comparar(9)
           → PrecoService.comparar(9)
               → PrecoRepository.findByProduto(9)
               → agrupar por mercado, manter mais recente
               → ordenar ascendente
           ← ComparadorDTO
       ← JSON
```

### 5.3. Otimização de lista
```
Cliente → GET /api/lista/{id}/otimizar
       → ListaController.otimizar(id)
           → OtimizadorService.calcular(id)
               → buscar itens da lista
               → para cada item, buscar preço vigente em cada mercado
               → montar "plano por mercado único" e "plano otimizado"
               → comparar totais
           ← ResultadoOtimizadorDTO (planos + economia)
       ← JSON
```

---

## 6. Decisões arquiteturais (ADR resumido)

### ADR-01 — Spring Boot como framework do backend
- **Contexto**: equipe acadêmica com familiaridade em Java.
- **Decisão**: usar Spring Boot 3.3.5.
- **Consequências**: produtividade alta, ecossistema maduro, comunidade ampla, build pesado comparado a alternativas "micro" (Quarkus, Micronaut), mas aceitável para o escopo.

### ADR-02 — Frontend em HTML/JS puro (sem framework)
- **Contexto**: escopo acadêmico, sem necessidade de reatividade complexa.
- **Decisão**: páginas estáticas servidas pelo próprio Spring Boot.
- **Consequências**: zero build step no frontend, curva de aprendizado baixa, simplicidade de deploy. Limita crescimento futuro — migração para React/Vue fica como opção se o produto evoluir.

### ADR-03 — Estado de sessão no cliente (localStorage)
- **Contexto**: simplicidade prioritária, sem necessidade de logout forçado server-side.
- **Decisão**: `localStorage('mc_usuario')` em vez de JWT/Session.
- **Consequências**: zero infraestrutura de token, logout é trivial. **Risco**: endpoints hoje não validam identidade no servidor — autorização é feita no cliente. Aceitável no escopo atual; deve ser endurecido antes de produção real (RNF05).

### ADR-04 — MariaDB como banco relacional
- **Contexto**: dados fortemente relacionais (mercados, produtos, preços, avaliações).
- **Decisão**: MariaDB 10.x+ (compatível com MySQL).
- **Consequências**: relações integras, SQL padrão, driver open-source. Não escala horizontalmente sem sharding — aceitável para o volume previsto.

### ADR-05 — Preços como registro append-only
- **Contexto**: necessidade de histórico e gráficos temporais.
- **Decisão**: cada publicação de preço cria nova linha; nunca há UPDATE.
- **Consequências**: histórico preservado naturalmente, queries de comparação precisam filtrar "mais recente" por mercado (consultando `MAX(dataHora)`). Crescimento linear da tabela.

### ADR-06 — Separação DTO × Model
- **Contexto**: evitar vazamento de estrutura interna do banco na API pública.
- **Decisão**: toda resposta de endpoint complexo usa DTO (`ComparadorDTO`, `DashboardComercioDTO`, etc.).
- **Consequências**: flexibilidade para evoluir o schema sem quebrar API.

### ADR-07 — Upload de imagens em diretório local
- **Contexto**: cadastro de produtos inclui imagem.
- **Decisão**: salvar em pasta `uploads/` local, referenciar por nome na coluna `imagem`.
- **Consequências**: simples, mas não distribuído. Se o projeto for hospedado em servidor, pasta precisa ser persistente (volume). **Alternativa futura**: S3/object storage.

---

## 7. Atributos de qualidade

| Qualidade | Como é atendida | Evidência |
|---|---|---|
| **Segurança** | BCrypt para senhas, constraints UNIQUE, DTOs que omitem senha | Model `Usuario`, Spring Security |
| **Desempenho** | Consultas indexadas por PK/FK, sem joins excessivos | Estrutura JPA |
| **Manutenibilidade** | Camadas bem separadas, Lombok, DTOs | Pastas `controller/`, `service/`, `dto/` |
| **Portabilidade** | JVM + MariaDB (cross-platform) | `pom.xml`, `application.properties` |
| **Testabilidade** | Services isoláveis (dependência por construtor via Lombok/Spring) | Pasta `service/` |
| **Usabilidade** | Interface responsiva, feedback visual (toasts, spinners) | CSS Grid/Flexbox, componentes JS |

---

## 8. Riscos arquiteturais e dívidas técnicas

| Risco/Dívida | Severidade | Mitigação |
|---|---|---|
| Banco centralizado na máquina do David | **Alta** | Sprint 1 — subir ao GitHub, cada membro roda local |
| Autorização depende do cliente (localStorage) | **Média** | Adicionar filtro/interceptor server-side na sprint 3 |
| Sem testes automatizados visíveis no repositório | **Média** | Criar `@SpringBootTest` para services críticos (otimizador, autenticação) |
| `ddl-auto = update` em produção é arriscado | **Média** | Migrar para Flyway/Liquibase antes de deploy real |
| Upload em pasta local não funciona em ambiente distribuído | **Baixa** (no escopo) | Trocar por object storage se sair de localhost |
| Sem CI/CD configurado | **Baixa** | GitHub Actions rodando `mvn test` — sprint 2 |
| Imagens na apresentação (feedback da banca) | **Média** | Revisar assets antes de 08/06 |

---

## 9. Roadmap arquitetural

### Sprint 1 (agora)
- Versionar projeto no GitHub.
- `.gitignore` correto (excluir `target/`, `uploads/` sensíveis, `application-local.properties`).
- README de setup para a equipe.

### Sprint 2
- Adicionar testes de integração dos 4 fluxos principais.
- Revisar imagens da interface (feedback da banca).

### Sprint 3
- Endurecer autorização server-side (filtro que lê token/sessão).
- Documentar evolução da interface no pitch.

### Sprint 4
- Congelar arquitetura para apresentação.
- Revisar slides (08/06), ajustes (15/06), banca (22/06).

### Pós-banca (fora do escopo acadêmico)
- Migrar frontend para SPA (React/Vue) se o produto ganhar tração.
- Object storage para uploads.
- Banco gerenciado (RDS/equivalente).
- Deploy com CI/CD.
