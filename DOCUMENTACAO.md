# MercadoCerto - Documentacao Completa do Sistema

> Plataforma de comparacao de precos e avaliacao de mercados.
> Permite que consumidores comparem precos, avaliem estabelecimentos e montem listas de compras inteligentes.

---

## Sumario

1. [Visao Geral](#1-visao-geral)
2. [Tecnologias](#2-tecnologias)
3. [Arquitetura](#3-arquitetura)
4. [Configuracao e Instalacao](#4-configuracao-e-instalacao)
5. [Banco de Dados](#5-banco-de-dados)
6. [API REST - Endpoints](#6-api-rest---endpoints)
7. [Models (Entidades JPA)](#7-models-entidades-jpa)
8. [Services (Logica de Negocio)](#8-services-logica-de-negocio)
9. [DTOs](#9-dtos)
10. [Frontend - Paginas](#10-frontend---paginas)
11. [Autenticacao e Seguranca](#11-autenticacao-e-seguranca)
12. [Funcionalidades Principais](#12-funcionalidades-principais)
13. [Estrutura de Arquivos](#13-estrutura-de-arquivos)

---

## 1. Visao Geral

**MercadoCerto** e uma aplicacao web fullstack para comparacao de precos entre mercados/supermercados. O sistema permite:

- **Consumidores**: comparar precos de produtos entre mercados, avaliar estabelecimentos, criar listas de compras otimizadas e localizar mercados no mapa.
- **Comerciantes**: cadastrar mercados e produtos, publicar/atualizar precos, acompanhar avaliacoes e metricas de reputacao no painel do comercio.

O backend e uma API REST construida com Spring Boot, e o frontend e composto por paginas HTML estaticas com JavaScript puro (sem frameworks).

---

## 2. Tecnologias

### Backend
| Tecnologia | Versao | Funcao |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.3.5 | Framework web |
| Spring Data JPA | 3.3.x | ORM / Acesso a dados |
| Spring Security | 6.x | Autenticacao (BCrypt) |
| MariaDB | 10.x+ | Banco de dados relacional |
| Lombok | 1.18.x | Reducao de boilerplate |
| Maven | 3.9.x | Gerenciamento de dependencias |

### Frontend
| Tecnologia | Funcao |
|---|---|
| HTML5 / CSS3 / JavaScript (ES6+) | Interface do usuario |
| Chart.js 4.4.1 | Graficos (historico de precos, reputacao) |
| Leaflet.js 1.9.4 | Mapa interativo (OpenStreetMap) |
| Font Awesome 6.5.0 | Icones (pagina inicial) |
| Google Fonts (Syne + DM Sans) | Tipografia |

### Design
- **Tema de cores**: Roxo (#4b0082) como cor primaria
- **Fontes**: Syne (titulos), DM Sans (corpo)
- **Layout**: Responsivo com CSS Grid e Flexbox
- **Componentes**: Cards, modais, toasts, spinners, tabs

---

## 3. Arquitetura

O projeto segue a arquitetura em camadas do Spring Boot:

```
[Frontend HTML/JS]
        |
        | HTTP (fetch API)
        v
[Controllers] -----> Recebem requisicoes HTTP, retornam JSON
        |
        v
[Services] --------> Logica de negocio, validacoes, calculos
        |
        v
[Repositories] ----> Acesso ao banco via Spring Data JPA
        |
        v
[MariaDB] ---------> Persistencia de dados
```

### Fluxo de autenticacao
A autenticacao e feita no frontend via `localStorage`. O backend valida credenciais no login e retorna os dados do usuario. Nao ha JWT nem sessoes server-side — o estado de login e mantido exclusivamente no navegador.

```
Login: POST /api/usuarios/login
  -> Backend valida senha (BCrypt)
  -> Retorna LoginResponseDTO
  -> Frontend salva em localStorage('mc_usuario')
  -> Paginas protegidas verificam localStorage no carregamento
```

---

## 4. Configuracao e Instalacao

### Pre-requisitos
- Java 17+
- Maven 3.9+
- MariaDB 10.x+ (rodando na porta 3306)

### Passo a passo

1. **Criar o banco de dados**:
```sql
CREATE DATABASE MercadoCerto;
```

2. **Executar o script de criacao** (opcional, JPA cria automaticamente):
```bash
mysql -u root -p MercadoCerto < mercdadocertocreate.sql
```

3. **Configurar credenciais** em `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/MercadoCerto
spring.datasource.username=root
spring.datasource.password=root
```

4. **Compilar e executar**:
```bash
mvn clean compile
mvn spring-boot:run
```

5. **Acessar**: http://localhost:8080

### Propriedades de configuracao

| Propriedade | Valor | Descricao |
|---|---|---|
| `server.port` | 8080 | Porta do servidor |
| `spring.jpa.hibernate.ddl-auto` | update | Auto-criacao/atualizacao de tabelas |
| `spring.jpa.show-sql` | true | Exibir queries no console |
| `spring.servlet.multipart.max-file-size` | 10MB | Tamanho maximo de upload |
| `uploads.path` | uploads | Diretorio para imagens de produtos |

---

## 5. Banco de Dados

### Diagrama ER (simplificado)

```
pais (1) ──── (N) cidade (1) ──── (N) endereco
                                         |
                                    (0..1)|
                                         v
usuario (1) ──── (N) lista_compras (1) ──── (N) item_lista
    |                                              |
    | (0..N)                                       | id_produto
    v                                              v
avaliacao (N) ────────── (1) mercado         produto
                              |                |
                              | (N)            | (N)
                              v                v
                            preco ◄────────── preco
                    (id_mercado + id_produto + valor + data_hora)
```

### Tabelas

#### `usuario`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_usuario | INT | PK, AUTO_INCREMENT | Identificador |
| nome_usuario | VARCHAR(100) | NOT NULL | Nome completo |
| email | VARCHAR(100) | UNIQUE, NOT NULL | E-mail |
| login | VARCHAR(50) | UNIQUE, NOT NULL | Nome de usuario |
| senha | VARCHAR(255) | NOT NULL | Hash BCrypt |
| tipo_conta | ENUM('USUARIO','COMERCIO') | NOT NULL | Tipo de conta |
| cnpj | VARCHAR(14) | UNIQUE, NULL | CNPJ (so comercio) |
| cpf | VARCHAR(11) | UNIQUE, NULL | CPF |
| data_nascimento | DATE | NULL | Data de nascimento |
| telefone | VARCHAR(20) | NULL | Telefone |
| id_endereco | INT | FK -> endereco | Endereco |

#### `mercado`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_mercado | INT | PK, AUTO_INCREMENT | Identificador |
| nome_mercado | VARCHAR(100) | NOT NULL | Nome do mercado |
| latitude | DOUBLE | NULL | Coordenada GPS |
| longitude | DOUBLE | NULL | Coordenada GPS |

#### `produto`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_produto | INT | PK, AUTO_INCREMENT | Identificador |
| nome_produto | VARCHAR(100) | NOT NULL | Nome do produto |
| marca | VARCHAR(100) | NULL | Marca |
| categoria | VARCHAR(50) | NULL | Categoria |
| codigo_barras | VARCHAR(50) | NULL | Codigo de barras |
| imagem | VARCHAR(255) | NULL | Nome do arquivo de imagem |
| preco | DOUBLE | NULL | Preco base de cadastro |
| validade | VARCHAR(20) | NULL | Data de validade |
| latitude | DOUBLE | NULL | Local de cadastro |
| longitude | DOUBLE | NULL | Local de cadastro |

#### `preco`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_preco | INT | PK, AUTO_INCREMENT | Identificador |
| id_produto | INT | FK -> produto | Produto referenciado |
| id_mercado | INT | FK -> mercado | Mercado que pratica o preco |
| valor | DECIMAL(10,2) | NOT NULL | Valor em reais |
| data_hora | DATETIME | DEFAULT NOW() | Data/hora da publicacao |

#### `avaliacao`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_avaliacao | INT | PK, AUTO_INCREMENT | Identificador |
| id_mercado | INT | FK -> mercado | Mercado avaliado |
| id_usuario | INT | FK -> usuario, NULL | Usuario (NULL = anonimo) |
| nota | INT | CHECK(1..5) | Nota de 1 a 5 |
| comentario | TEXT | NULL | Comentario opcional |
| data_avaliacao | DATETIME | DEFAULT NOW() | Data/hora |

#### `lista_compras`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_lista | INT | PK, AUTO_INCREMENT | Identificador |
| id_usuario | INT | FK -> usuario | Dono da lista |
| nome_lista | VARCHAR(100) | NOT NULL | Nome da lista |
| data_criacao | DATETIME | DEFAULT NOW() | Data de criacao |

#### `item_lista`
| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_item | INT | PK, AUTO_INCREMENT | Identificador |
| id_lista | INT | FK -> lista_compras (CASCADE) | Lista pai |
| id_produto | INT | NOT NULL | Produto referenciado |
| quantidade | INT | DEFAULT 1 | Quantidade desejada |

#### `endereco`, `cidade`, `pais`
Tabelas auxiliares de localizacao (endereco do usuario/mercado). Estrutura hierarquica: pais -> cidade -> endereco.

---

## 6. API REST - Endpoints

Base URL: `http://localhost:8080`

### Usuarios (`/api/usuarios`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| POST | `/api/usuarios/register` | Cadastrar usuario | `{ nomeUsuario, email, login, senha, tipoConta, cnpj? }` |
| POST | `/api/usuarios/login` | Fazer login | `{ login, senha }` |
| GET | `/api/usuarios` | Listar todos | - |
| GET | `/api/usuarios/{id}` | Buscar por ID | - |
| GET | `/api/usuarios/login/{login}` | Buscar por login | - |
| GET | `/api/usuarios/email/{email}` | Buscar por email | - |

**Resposta de login** (`LoginResponseDTO`):
```json
{
  "idUsuario": 1,
  "nomeUsuario": "Joao Silva",
  "email": "joao@email.com",
  "login": "joao",
  "tipoConta": "USUARIO"
}
```

### Produtos (`/api/produtos`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| GET | `/api/produtos` | Listar todos os produtos | - |
| POST | `/api/produtos/cadastrar` | Cadastrar produto | `multipart/form-data`: produto (JSON), idMercado, imagem? |

**Exemplo de cadastro** (multipart):
```
produto: {"nomeProduto":"Arroz","marca":"Tio Joao","categoria":"Graos","codigoBarras":"789","preco":5.99,"validade":"2026-12-31"}
idMercado: 1
imagem: [arquivo]
```

### Mercados (`/api/mercados`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| GET | `/api/mercados` | Listar todos | - |
| GET | `/api/mercados/{id}` | Buscar por ID | - |
| POST | `/api/mercados` | Cadastrar mercado | `{ nomeMercado, latitude, longitude }` |
| PUT | `/api/mercados/{id}` | Atualizar mercado | `{ nomeMercado, latitude, longitude }` |
| DELETE | `/api/mercados/{id}` | Remover mercado | - |
| GET | `/api/mercados/proximos` | Buscar proximos | `?latitude=-23.55&longitude=-46.63&raioKm=5` |

### Precos (`/api/precos`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| POST | `/api/precos` | Publicar preco | `{ idProduto, idMercado, valor }` |
| GET | `/api/precos/comparar` | Comparar precos de produto | `?produto={idProduto}` |
| GET | `/api/precos/historico` | Historico de precos | `?produto={idProduto}&mercado={idMercado}` |
| GET | `/api/precos/mercado/{idMercado}` | Catalogo do mercado | - |

**Resposta de comparacao** (`ComparadorDTO`):
```json
{
  "idProduto": 9,
  "nomeProduto": "Arroz Integral",
  "precos": [
    {
      "idMercado": 1,
      "nomeMercado": "Supermercado ABC",
      "valor": 5.99,
      "dataHora": "2026-03-27T20:40:36"
    },
    {
      "idMercado": 3,
      "nomeMercado": "Condor",
      "valor": 7.50,
      "dataHora": "2026-03-28T10:15:00"
    }
  ]
}
```

### Avaliacoes (`/api/avaliacoes`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| POST | `/api/avaliacoes` | Enviar avaliacao | `{ idMercado, nota, comentario?, dataAvaliacao }` |
| GET | `/api/avaliacoes/mercado/{idMercado}` | Avaliacoes de um mercado | - |
| GET | `/api/avaliacoes/media/{idMercado}` | Media de notas | - |
| GET | `/api/avaliacoes/total/{idMercado}` | Total de avaliacoes | - |
| GET | `/api/avaliacoes/ranking` | Ranking de mercados | - |

**Resposta de ranking**:
```json
[
  { "id_mercado": 1, "nome_mercado": "Mercado X", "media": 4.5, "votos": 12 },
  { "id_mercado": 3, "nome_mercado": "Condor", "media": 4.2, "votos": 8 }
]
```

### Lista de Compras (`/api/lista`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| GET | `/api/lista/usuario/{idUsuario}` | Listas do usuario | - |
| GET | `/api/lista/{idLista}` | Detalhes da lista | - |
| POST | `/api/lista` | Criar lista | `?idUsuario={id}&nomeLista={nome}` |
| POST | `/api/lista/{idLista}/item` | Adicionar item | `{ idProduto, quantidade }` |
| DELETE | `/api/lista/{idLista}/item/{idProduto}` | Remover item | - |
| DELETE | `/api/lista/{idLista}` | Excluir lista | - |
| GET | `/api/lista/{idLista}/otimizar` | Otimizar lista | - |

**Resposta de otimizacao** (`ResultadoOtimizadorDTO`):
```json
{
  "idLista": 1,
  "nomeLista": "Compras do mes",
  "planos": [
    {
      "idMercado": 1,
      "nomeMercado": "Mercado X",
      "latitude": -25.4295,
      "longitude": -49.2712,
      "itens": [
        { "idProduto": 9, "nomeProduto": "Arroz", "idMercado": 1, "nomeMercado": "Mercado X", "valor": 5.99, "quantidade": 2 }
      ],
      "subtotal": 11.98
    }
  ],
  "totalOtimizado": 45.90,
  "totalMercadoMaisBarato": 52.30,
  "economia": 6.40
}
```

### Painel do Comercio (`/api/painel`)

| Metodo | Endpoint | Descricao | Body/Params |
|---|---|---|---|
| GET | `/api/painel/dashboard/{idMercado}` | Dashboard do mercado | - |

**Resposta** (`DashboardComercioDTO`):
```json
{
  "mediaGeral": 4.3,
  "totalAvaliacoes": 15,
  "totalProdutos": 42,
  "evolucaoMensal": [
    { "mes": "2026-01", "mediaMes": 4.0, "totalAvaliacoes": 5 },
    { "mes": "2026-02", "mediaMes": 4.5, "totalAvaliacoes": 10 }
  ],
  "avaliacoesRecentes": [...]
}
```

---

## 7. Models (Entidades JPA)

### Usuario
- **Tabela**: `usuario`
- **Campos principais**: idUsuario, nomeUsuario, email, login, senha (BCrypt), tipoConta (USUARIO/COMERCIO), cnpj
- **Anotacoes**: `@JsonProperty(access = WRITE_ONLY)` na senha (nunca retornada na API)
- **Enum `TipoConta`**: USUARIO, COMERCIO

### Mercado
- **Tabela**: `mercado`
- **Campos**: idMercado, nomeMercado, latitude, longitude
- **Usa Lombok**: `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`

### Produto
- **Tabela**: `produto`
- **Campos**: idProduto, nomeProduto, marca, categoria, codigoBarras, imagem, preco, validade, latitude, longitude
- **Campo `idMercado`**: marcado como `@Transient` (nao persiste no banco, usado apenas no fluxo de cadastro para criar o registro de preco)

### Preco
- **Tabela**: `preco`
- **Campos**: idPreco, idProduto, idMercado, valor (BigDecimal), dataHora
- **Auto-timestamp**: `@PrePersist` define dataHora automaticamente

### Avaliacao
- **Tabela**: `avaliacao`
- **Campos**: idAvaliacao, idMercado, idUsuario (nullable), nota (1-5), comentario, dataAvaliacao
- **Avaliacoes anonimas**: idUsuario pode ser NULL

### ListaCompras
- **Tabela**: `lista_compras`
- **Campos**: idLista, idUsuario, nomeLista, dataCriacao
- **Relacao**: `@OneToMany` com ItemLista (cascade ALL, orphanRemoval)

### ItemLista
- **Tabela**: `item_lista`
- **Campos**: idItem, lista (FK), idProduto, quantidade
- **Relacao**: `@ManyToOne` com ListaCompras

---

## 8. Services (Logica de Negocio)

### UsuarioService
- **Cadastro**: Valida email unico, login unico, CNPJ valido (para COMERCIO com validacao de digitos verificadores), criptografa senha com BCrypt
- **Login**: Busca por login OU email, verifica senha com BCrypt

### ProdutoService
- **Cadastro com imagem**: Salva imagem no disco (`uploads/`), nome do arquivo: `timestamp_nomeOriginal`
- **Criacao automatica de preco**: Se `idMercado` for informado no cadastro, cria registro na tabela `preco`

### PrecoService
- **Comparacao**: Busca preco atual de um produto em todos os mercados, retorna lista ordenada por valor (mais barato primeiro)
- **Historico**: Retorna evolucao de precos de um produto em um mercado especifico

### AvaliacaoService
- **Ranking**: Query nativa que agrupa avaliacoes por mercado, calcula media e total de votos, ordena por media decrescente

### ListaComprasService
- **Otimizador**: Algoritmo que distribui a lista de compras entre mercados para minimizar o custo total:
  1. Busca o melhor preco de cada produto da lista em todos os mercados
  2. Agrupa itens por mercado (cria "planos de compra")
  3. Calcula economia comparando o custo otimizado com o custo de comprar tudo no melhor mercado unico

### MercadoService
- **Busca por proximidade**: Usa formula de Haversine para encontrar mercados dentro de um raio em km

---

## 9. DTOs

Todos os DTOs sao Java Records (imutaveis):

| DTO | Usado em | Campos |
|---|---|---|
| `LoginResponseDTO` | Login | idUsuario, nomeUsuario, email, login, tipoConta |
| `ComparadorDTO` | Comparar precos | idProduto, nomeProduto, List\<PrecoMercadoDTO\> |
| `PrecoMercadoDTO` | Comparar precos | idMercado, nomeMercado, valor, dataHora |
| `ResultadoOtimizadorDTO` | Otimizar lista | idLista, nomeLista, planos, totalOtimizado, totalMercadoMaisBarato, economia |
| `PlanoMercadoDTO` | Otimizar lista | idMercado, nomeMercado, lat, lng, itens, subtotal |
| `ItemOtimizadoDTO` | Otimizar lista | idProduto, nomeProduto, idMercado, nomeMercado, valor, quantidade |
| `DashboardComercioDTO` | Painel comercio | mediaGeral, totalAvaliacoes, totalProdutos, evolucaoMensal, avaliacoesRecentes |
| `ReputacaoMensalDTO` | Painel comercio | mes, mediaMes, totalAvaliacoes |
| `ItemListaRequestDTO` | Adicionar item | idProduto, quantidade |

---

## 10. Frontend - Paginas

### Paginas publicas (sem login)

| Pagina | Arquivo | Descricao |
|---|---|---|
| Home | `index.html` | Landing page com grid de acoes rapidas. Adapta-se ao usuario logado (esconde login/cadastro, mostra painel) |
| Login | `login.html` | Formulario de login com layout split-screen. Redireciona logados automaticamente |
| Cadastro | `cadastro.html` | Cadastro com toggle Consumidor/Supermercado. Campo CNPJ aparece para comercio. Validacao de CNPJ com digitos verificadores |
| Produtos | `lista-produtos.html` | Grid de cards com filtros (nome, categoria, mercado) e ordenacao (nome, preco, categoria, recente). Botao "Comparar precos" em cada card |
| Mercados | `mercados.html` | Lista de mercados com nota media e botao "Avaliar". Modal de avaliacao com estrelas e comentario |
| Ranking | `ranking.html` | Ranking de mercados por nota media. Top 3 com medalhas |
| Mapa | `mapa.html` | Mapa interativo (Leaflet/OpenStreetMap). Busca produtos por nome e exibe no mapa com popup (preco, mercado, imagem) |
| Comparar | `comparar.html` | Seleciona produto e ve tabela de precos por mercado (mais barato destacado). Grafico de historico com Chart.js. Aceita parametro `?produto=ID` na URL |
| Lista de Compras | `lista-compra.html` | Criacao e gerenciamento de listas via localStorage. Interface de checklist com toggle de itens |

### Paginas protegidas (requer login)

| Pagina | Arquivo | Requer | Descricao |
|---|---|---|---|
| Dashboard | `dashboard.html` | Login (USUARIO) | Painel do consumidor: stats, listas recentes, top mercados, produtos mais baratos |
| Painel Comercio | `painel-comercio.html` | Login (COMERCIO) | Painel do comerciante com 4 abas: Reputacao (grafico mensal), Catalogo, Publicar Preco, Avaliacoes recebidas |
| Cadastrar Produto | `cadastro-produto.html` | Login (COMERCIO) | Formulario com upload de imagem (validacao tipo/tamanho), selecao de mercado |
| Cadastrar Mercado | `cadastro-mercado.html` | Login (COMERCIO) | Formulario com botao de geolocalizacao automatica |

### Arquivos JavaScript

| Arquivo | Descricao |
|---|---|
| `js/main.js` | Utilitarios compartilhados: constante API, helpers de auth (getUsuarioLogado, setUsuarioLogado, logout, isComercio, requireLogin, requireComercio), toast notifications, validacao (email, CNPJ com formatacao), spinner de loading, display de estrelas, destaque de navegacao, hamburger menu |
| `js/header.js` | Injecao dinamica do header: monta navegacao baseada no estado de login e tipo de conta, dropdown de usuario com logout |

### Arquivos CSS

| Arquivo | Descricao |
|---|---|
| `css/style.css` | Stylesheet unico com design system completo: tokens (cores, sombras, radius), tipografia, grid, cards, formularios, botoes, badges, toasts, modais, estrelas, rankings, paginas de auth, responsividade |

---

## 11. Autenticacao e Seguranca

### Backend
- **BCrypt** (strength 10) para hash de senhas
- **CSRF desabilitado** (API REST stateless)
- **CORS** configurado para `localhost:5500` e `127.0.0.1:5500`
- **Todos os endpoints sao publicos** (sem autorizacao server-side)
- **Upload seguro**: Nomes de arquivo sanitizados com timestamp + nome original

### Frontend
- **localStorage** (`mc_usuario`) armazena dados do usuario logado
- **Protecao de paginas**: `requireLogin()` e `requireComercio()` verificam localStorage e redirecionam imediatamente se nao autenticado
- **Senhas**: Nunca retornadas pela API (`@JsonProperty(WRITE_ONLY)`)
- **Validacao de CNPJ**: Algoritmo completo de verificacao de digitos no frontend e backend

### Fluxo de autenticacao

```
1. Usuario acessa /login.html
2. Preenche credenciais (login ou email + senha)
3. POST /api/usuarios/login
4. Backend busca por login OU email
5. Verifica senha com BCrypt
6. Retorna LoginResponseDTO (sem senha)
7. Frontend salva em localStorage
8. Header se adapta (mostra nome, avatar, menu dropdown)
9. Paginas protegidas verificam localStorage no carregamento
```

---

## 12. Funcionalidades Principais

### 12.1 Comparacao de Precos
O usuario seleciona um produto e visualiza uma tabela com os precos praticados em todos os mercados, ordenados do mais barato ao mais caro. Inclui:
- Badge "Mais barato" no primeiro
- Diferenca percentual entre mais barato e mais caro
- Grafico de historico de precos por mercado (filtro 7/30/90 dias)
- Link direto de `lista-produtos.html` via `?produto=ID`

### 12.2 Avaliacao de Mercados
Qualquer usuario pode avaliar mercados com nota de 1 a 5 estrelas e comentario opcional. O sistema:
- Aceita avaliacoes anonimas (sem login)
- Calcula media e total de votos por mercado
- Gera ranking geral ordenado por media

### 12.3 Otimizador de Lista de Compras
Dado uma lista de compras do usuario, o algoritmo:
1. Busca o melhor preco de cada item em todos os mercados
2. Distribui os itens entre mercados para minimizar custo total
3. Gera "planos de compra" por mercado com subtotais
4. Calcula economia comparando com comprar tudo em um unico mercado

### 12.4 Mapa Interativo
Mapa com Leaflet.js e tiles do OpenStreetMap:
- Busca produtos por nome e exibe marcadores nos mercados que vendem
- Popup com nome, preco, marca e imagem do produto
- Botao "Ver Mercados" mostra todos os mercados no mapa
- Auto-zoom para enquadrar marcadores

### 12.5 Painel do Comercio
Dashboard exclusivo para contas COMERCIO:
- **Reputacao**: Grafico de barras com evolucao mensal da nota media
- **Catalogo**: Lista de precos atuais publicados pelo mercado
- **Publicar Preco**: Formulario para criar/atualizar preco de um produto
- **Avaliacoes**: Lista de avaliacoes recebidas com nota, comentario e data

### 12.6 Dashboard do Consumidor
Painel para contas USUARIO:
- Cards de stats: total de produtos, mercados, listas e melhor mercado
- Listas de compras recentes
- Top mercados por avaliacao
- Produtos com menores precos

---

## 13. Estrutura de Arquivos

```
startup/
├── pom.xml
├── mercdadocertocreate.sql          # Script de criacao do banco
├── migrar_banco.sql                 # Script de migracao
├── uploads/                         # Imagens de produtos enviadas
│
└── src/main/
    ├── java/com/mercadocerto/
    │   ├── MercadocertoApplication.java
    │   │
    │   ├── config/
    │   │   ├── GlobalExceptionHandler.java   # Tratamento global de erros
    │   │   ├── SecurityConfig.java           # BCrypt, CSRF, permissoes
    │   │   └── WebConfig.java                # CORS, servir uploads
    │   │
    │   ├── controller/
    │   │   ├── AvaliacaoController.java      # /api/avaliacoes
    │   │   ├── DashboardController.java      # /api/painel
    │   │   ├── ListaComprasController.java   # /api/lista
    │   │   ├── MercadoController.java        # /api/mercados
    │   │   ├── PrecoController.java          # /api/precos
    │   │   ├── ProdutoController.java        # /api/produtos
    │   │   └── UsuarioController.java        # /api/usuarios
    │   │
    │   ├── dto/
    │   │   └── DTOs.java                     # Todos os records DTO
    │   │
    │   ├── model/
    │   │   ├── Avaliacao.java
    │   │   ├── ItemLista.java
    │   │   ├── ListaCompras.java
    │   │   ├── Mercado.java
    │   │   ├── Preco.java
    │   │   ├── Produto.java
    │   │   └── Usuario.java
    │   │
    │   ├── repository/
    │   │   ├── AvaliacaoRepository.java
    │   │   ├── ListaComprasRepository.java
    │   │   ├── MercadoRepository.java
    │   │   ├── PrecoRepository.java
    │   │   ├── ProdutoRepository.java
    │   │   └── UsuarioRepository.java
    │   │
    │   └── service/
    │       ├── AvaliacaoService.java
    │       ├── ListaComprasService.java
    │       ├── MercadoService.java
    │       ├── PrecoService.java
    │       ├── ProdutoService.java
    │       └── UsuarioService.java
    │
    └── resources/
        ├── application.properties
        └── static/
            ├── css/
            │   └── style.css                 # Design system completo
            ├── js/
            │   ├── main.js                   # Utilitarios globais
            │   └── header.js                 # Injecao do header
            │
            ├── index.html                    # Home / Landing
            ├── login.html                    # Login
            ├── cadastro.html                 # Registro de usuario
            ├── dashboard.html                # Painel do consumidor
            ├── painel-comercio.html          # Painel do comercio
            ├── lista-produtos.html           # Grid de produtos
            ├── cadastro-produto.html         # Cadastro de produto
            ├── mercados.html                 # Lista de mercados + avaliacao
            ├── cadastro-mercado.html         # Cadastro de mercado
            ├── ranking.html                  # Ranking de mercados
            ├── comparar.html                 # Comparacao de precos
            ├── lista-compra.html             # Lista de compras (localStorage)
            └── mapa.html                     # Mapa interativo
```

---

*Documentacao gerada em 31/03/2026.*
